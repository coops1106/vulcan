package com.opencloudbrokers.vulcan.provision.vm;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;


import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;


import groovy.util.logging.Slf4j
import org.jclouds.compute.options.TemplateOptions
import java.util.concurrent.TimeoutException
import com.opencloudbrokers.vulcan.test.InfrastructureChecks
import com.opencloudbrokers.vulcan.VirtualMachine
import org.jclouds.compute.ComputeService
import org.jclouds.ec2.compute.options.EC2TemplateOptions;

@Slf4j
public class JCloudsProvision implements VirtualMachineProvisioner {

  private File tk = File.createTempFile("vulcan", "tpk")
  private String spec

  private String privateKey = System.getenv("VULCAN_PRIVATE_KEY")
  private String publicKey = System.getenv("VULCAN_PUBLIC_KEY")

  public JCloudsProvision(String specificationName) {
    spec = specificationName.toLowerCase()
    tk.delete()
    tk.mkdirs()
  }

  void provision(List virtualMachines) {

    if (!virtualMachines) {
      log.info "No VMs to create using jclouds"
      return
    }
    log.info("Using JClouds to connect to remote providers");

//    context = JCloudsConnector.context

    //TODO, Datacentre locations (US West 2, London etc)

// here's an example of the portable api
//    Set<? extends Location> locations =
//        context.getComputeService().listAssignableLocations();

    //Set<? extends Image> images = context.getComputeService().listImages();

    //TODO, do we need to decide/ specify external IP addresses (elastic IPs)?
    //ie, are these VMs directly accessible by us

    try {

      //log.info "In provider aws-ec2, locations= ${context.computeService.listAssignableLocations()}"

      virtualMachines.each { VirtualMachine vm ->

        def context = JCloudsConnector.getContext(vm.name)

        if (System.properties["vm.${vm.name}.id"]) {
          NodeMetadata metadata = context.computeService.getNodeMetadata(System.properties["vm.${vm.name}.id"])

          vm.delegate = new JCloudsVirtualMachineDelegate(
                  metadata,
                  tk,
                  context.computeService)
          if (System.properties["vm.${vm.name}.user"]) {
            vm.delegate.userName = System.properties["vm.${vm.name}.user"]
            log.info("Using login ${vm.delegate.userName}@${vm.name}")
          }
        } else {

          //TODO, specifically pick the image to use.
          TemplateBuilder tb = context.computeService.templateBuilder()
              .os64Bit(true)
              .osFamily(OsFamily.UBUNTU)
              .options(TemplateOptions.Builder.inboundPorts(vm.openPorts as int[])
              .authorizePublicKey(new File(publicKey).text))
              .osVersionMatches("10.04")
              .minCores(vm.cpuCount)
              .minDisk(vm.disk)
              .minRam(vm.memory)

          if (System.properties["vm.${vm.name}.location"]) {
            log.info("VM ${vm.name} loaded to specific location ${System.properties["vm.${vm.name}.location"]}")
            tb.locationId(System.properties["vm.${vm.name}.location"])
          }

          Template template = tb.build();

          log.info "Creating node ${vm.name} in group $spec"
          Set<? extends NodeMetadata> nodes = context.computeService.createNodesInGroup(spec, 1, template);

          nodes.each {  NodeMetadata metadata ->
            log.info "VM Created with login user ${metadata.credentials.user}, have installed local key, provider key (${metadata.credentials.privateKey != null})"

            vm.delegate = new JCloudsVirtualMachineDelegate(metadata, tk, context.computeService)
          }
          log.info "Node ${vm.name} created, delegate is now connected to provider hosted VM"
        }
      }
      awaitBoot(virtualMachines)
    } catch (Exception e) {
      log.error "Error creating nodes......"
      //TODO, kill any nodes that have been created...
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  void awaitBoot(List<VirtualMachine> vms) {
    boolean booted = true
    int timeout = 1000 * 60 * 5

    long started = System.currentTimeMillis()

    vms.each {
      if (!it.running) {
        booted = false
      }
    }

    while (!booted) {
      checkTimeOut(started, timeout);
      Thread.sleep(1000)

      vms.each {
        if (!it.running) {
          booted = false
        }
      }
    }
    //this ensures we can log into the VM with SSH, ie, the OS has fully booted.
    //stops us needing to do noddy sleeps for settling.
    vms.each { vm ->
      InfrastructureChecks.tryTillTimeout(120000) {
        vm.executeCommandAndReturn("df -kah").sysOutput.contains("proc")
      }
    }
    log.info "All nodes are UP"
  }

  void checkTimeOut(long started, long timeout) {
    if (System.currentTimeMillis() > started + timeout) {
      throw new TimeoutException("Waiting for booting Vms for ${timeout}ms, they didn't boot in time")
    }
  }

  public void disconnect() {
    tk.deleteDir()
//    context?.close()
  }

}

@Slf4j
class JCloudsVirtualMachineDelegate implements VirtualMachineDelegate {
  private NodeMetadata metaData;
  private File tk
  private File priv
  private ComputeService computeService
  private String overrideUsername

  JCloudsVirtualMachineDelegate(NodeMetadata metaData, File tk, ComputeService computeService) {
    this.metaData = metaData;
    this.tk = tk

    this.computeService = computeService
    //TODO, this is extracting the key and then writing it locally, is this secure or wise?
    priv = new File(tk, UUID.randomUUID().toString())
    if (metaData.credentials?.privateKey != null) {
      log.info("Using JClouds provided private key from the provider API")
      priv << this.metaData.credentials.privateKey
    } else {
      log.warn("Using local key, as no key is available from the provider.")
      priv << new File(System.getenv("VULCAN_PRIVATE_KEY")).text
    }
  }

  @Override
  String getIdentifier() {
    return null  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public VirtualMachine.CommandReturn executeCommandAndReturn(String command) {
    return SSHUtils.executeCommand(command, this)
  }

  @Override
  public String getUserName() {
    if (overrideUsername) {
      return overrideUsername;
    }
    return metaData.getCredentials().getUser();
  }

  @Override
  void setUserName(String userName) {
    overrideUsername = userName
  }

  @Override
  public File getSshPrivateKey() {
    return priv
  }

  @Override
  public String getIpAddressToConnectTo() {
    return metaData.getPublicAddresses().iterator().next();
  }

  @Override
  public int getPortToConnectTo() {
    return 22;
  }

  @Override
  boolean isRunning() {
    return metaData.status == NodeMetadata.Status.RUNNING
  }

  @Override
  void delete() {
    computeService.destroyNode(metaData.id)
  }

  @Override
  String getProviderName() {
    return computeService.context.providerSpecificContext.providerMetadata.name
  }
}
