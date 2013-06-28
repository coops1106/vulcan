package com.simplicityitself.vulcan.provision.vm.jclouds

import com.google.common.collect.ImmutableSet
import com.google.common.util.concurrent.ListenableFuture
import com.simplicityitself.vulcan.VirtualMachineImage
import com.simplicityitself.vulcan.provision.vm.VirtualMachineProvisioner
import org.jclouds.ContextBuilder
import org.jclouds.aws.ec2.AWSEC2ApiMetadata
import org.jclouds.aws.ec2.AWSEC2Client
import org.jclouds.compute.ComputeServiceContext
import org.jclouds.compute.domain.ComputeMetadata
import org.jclouds.compute.domain.HardwareBuilder
import org.jclouds.compute.domain.Image
import org.jclouds.compute.domain.ImageBuilder
import org.jclouds.compute.domain.ImageTemplate
import org.jclouds.compute.domain.ImageTemplateBuilder
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template
import org.jclouds.compute.domain.TemplateBuilder;

import groovy.util.logging.Slf4j
import org.jclouds.compute.options.TemplateOptions
import org.jclouds.ec2.compute.options.EC2TemplateOptions

import java.util.concurrent.TimeoutException
import com.simplicityitself.vulcan.test.InfrastructureChecks
import com.simplicityitself.vulcan.VirtualMachine
import org.jclouds.compute.ComputeService

@Slf4j
public class JCloudsProvision implements VirtualMachineProvisioner {

  private File tk = File.createTempFile("vulcan", "tpk")
  private String spec

  private String privateKey = System.getenv("VULCAN_PRIVATE_KEY")
  private String publicKey = System.getenv("VULCAN_PUBLIC_KEY")

  Map<VirtualMachine, ComputeServiceContext> vmToContext = [:]
  Map<VirtualMachineImage, ComputeServiceContext> imageToContext = [:]


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
    log.info("Using JClouds to connect to remote providers for ${virtualMachines}");

//    context = JCloudsConnector.context

    //TODO, Datacentre locations (US West 2, London etc)

// here's an example of the portable api
//    Set<? extends Location> locations =
//        context.getComputeService().listAssignableLocations();

    //Set<? extends Image> images = context.getComputeService().listImages();

    //TODO, do we need to decide/ specify external IP addresses (elastic IPs)?
    //ie, are these VMs directly accessible by us

    try {
      virtualMachines.each { VirtualMachine vm ->
        initVm(vm)
      }
      awaitBoot(virtualMachines)
    } catch (Exception e) {
      log.error "Error creating nodes......"
      //TODO, kill any nodes that have been created...
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
  }

  private void initVm(VirtualMachine vm) {
    log.info("Initialising VM ${vm.name}");

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
              .osVersionMatches("12.04")
              .minCores(vm.cpuCount)
              .minDisk(vm.disk)
              .minRam(vm.memory)

      if (System.properties["vm.${vm.name}.location"]) {
        log.info("VM ${vm.name} loaded to specific location ${System.properties["vm.${vm.name}.location"]}")
        tb.locationId(System.properties["vm.${vm.name}.location"])
      }

      Template template = tb.build();
      log.info "Selected template ${template.image.providerId}"

      //This sets up EBS.
//          template.options.as(EC2TemplateOptions)
//            .mapEBSSnapshotToDeviceName("/dev/sdo", "snapshotiddd", 10, true)
//            .mapEphemeralDeviceToDeviceName("/dev/sdn", "ephemera10")
//            .mapNewVolumeToDeviceName("/dev/sdn", (int) vm.disk/ 1000, true)
//            .

      //get hold of an existing snapshot...?
      //.mapEBSSnapshotToDeviceName("/dev/sdo", )

      log.info "Creating node ${vm.name} in group $spec"
      Set<? extends NodeMetadata> nodes = context.computeService.createNodesInGroup(spec, 1, template);

      nodes.each { NodeMetadata metadata ->

        log.info "VM Created with login user ${metadata.credentials.user}/ ${metadata.credentials.credential}, have installed local key, provider key (${metadata.credentials.privateKey != null})"

        vm.delegate = new JCloudsVirtualMachineDelegate(metadata, tk, context.computeService)
      }
      vmToContext[vm] = context

      log.info "Node ${vm.name} created, delegate is now connected to provider hosted VM"
    }
  }

  @Override
  boolean isRunning(VirtualMachine vm) {
    withContext(vm) { ComputeServiceContext ctx ->
      ctx.computeService.getNodeMetadata(vm.identifier).status == NodeMetadata.Status.RUNNING
    }
  }

  @Override
  void deleteFromProvider(VirtualMachine vm) {

    withContext(vm) { ComputeServiceContext context ->
      context.computeService.destroyNode(vm.identifier)
    }

    vm
  }

  private def withContext(VirtualMachine vm, Closure ex) {
    ComputeServiceContext context = vmToContext[vm]

    ex(context)
  }

  private def withContext(VirtualMachineImage vm, Closure ex) {
    ComputeServiceContext context = imageToContext[vm]

    ex(context)
  }

  @Override
  VirtualMachineImage generateProviderImageFrom(String imageName, VirtualMachine vm) {
    VirtualMachineImage ret = new VirtualMachineImage()

    withContext(vm) { ComputeServiceContext context ->

      //TODO, this is how to access the client directly.
      //AWSEC2Client client = ContextBuilder.newBuilder(vm.providerName).buildApi(AWSEC2Client)

      ImageTemplate template = context.computeService.imageExtension.get().buildImageTemplateFromNode(imageName, vm.identifier)
      ListenableFuture<Image> future = context.computeService.imageExtension.get().createImage(template)

      Image image = future.get()

      imageToContext[ret] = context
      ret.tags = image.tags
      ret.providerId = image.providerId
      ret.id = image.id
    }
    ret
  }

  @Override
  void deleteFromProvider(VirtualMachineImage virtualMachineImage) {
    withContext(virtualMachineImage) { ComputeServiceContext context ->
      context.computeService.imageExtension.get().deleteImage(virtualMachineImage.id)
      imageToContext.remove(virtualMachineImage)
    }
  }

  @Override
  VirtualMachineImage getImage(String id) {
      throw new IllegalStateException("Wibble")
  }

  @Override
  VirtualMachineImage addTagToImage(VirtualMachineImage image, String tag) {
    /*
    TODO, this recreates the entire image in order to add a tag. This seems wasteful...
     */
    VirtualMachineImage ret = new VirtualMachineImage()

    withContext(image) { ComputeServiceContext context ->
      def old = context.computeService.getImage(image.id)
      def tags = new HashSet(old.tags)
      tags << tag

      Image newImage = ImageBuilder.fromImage(old).tags(["tag"]).build()

      context.computeService.imageExtension.get().deleteImage(image.id)

      ret.tags = newImage.tags
      ret.providerId = newImage.providerId
      ret.id = newImage.id
    }

    return ret
  }

  void awaitBoot(List<VirtualMachine> vms) {
    boolean booted = true
    int timeout = 1000 * 60 * 5

    long started = System.currentTimeMillis()

    vms.each {
      if (!isRunning(it)) {
        booted = false
      }
    }

    while (!booted) {
      checkTimeOut(started, timeout);
      Thread.sleep(1000)

      vms.each {
        if (!isRunning(it)) {
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
    log.info "On provider All nodes are UP"
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
