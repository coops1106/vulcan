package com.simplicityitself.vulcan;

import com.simplicityitself.vulcan.provision.vm.*;

import java.util.ArrayList;
import java.util.List;

public class Vulcan {

  private List<VirtualMachineProvisioner> virtualMachineProvisioners = new ArrayList<VirtualMachineProvisioner>();

  private VagrantVirtualMachineProvision vagrantProvision;
  private DockerVirtualMachineProvision dockerProvision;

  public static Vulcan withDefaultSettings() {
    Vulcan vulcan = new Vulcan();
    return vulcan;
  }

  public Vulcan initialiseInfrastructure(String infraName, VirtualMachine ... virtualMachines) {

    List<VirtualMachine> vagrantVMs = getVagrantVms(virtualMachines);
    List<VirtualMachine> dockerVMs = getDockerVms(virtualMachines);
    List<VirtualMachine> jcloudsVMs = getJcloudsVms(virtualMachines);

    VirtualMachineProvisioner jCloudsProvision = new JCloudsProvision(infraName);
    virtualMachineProvisioners.add(jCloudsProvision);

    if (vagrantVMs.size() > 0) {
      System.out.println(vagrantVMs.size() + " Vagrant Managed VMS");
      vagrantProvision = new VagrantVirtualMachineProvision(infraName);
      virtualMachineProvisioners.add(vagrantProvision);
      vagrantProvision.provision(vagrantVMs);
    }
    if (dockerVMs.size() > 0) {
      System.out.println(dockerVMs.size() + " Docker Managed VMS");
      dockerProvision = new DockerVirtualMachineProvision(infraName);
      virtualMachineProvisioners.add(dockerProvision);
      dockerProvision.provision(dockerVMs);
    }

    jCloudsProvision.provision(jcloudsVMs);

    //at this point, we have our virtual machines.
    return this;
  }

  public VagrantVirtualMachineProvision getVagrantProvision() {
    return vagrantProvision;
  }

  public void disconnect() {
    //We should be eating exceptions here to give all resources a chance to clean up...
    for (VirtualMachineProvisioner virtualMachineProvisioner : virtualMachineProvisioners) {
      virtualMachineProvisioner.disconnect();
    }
  }

  public List<VirtualMachine> getAllVirtualMachines() {
//    if (provider.toLowerCase().trim() == "vagrant") {
//      return Collections.emptyList();
//    }
    return new JCloudsEnumerator().getAllVirtualMachines();
  }

  private List<VirtualMachine> getVagrantVms(VirtualMachine ... vms) {
    List <VirtualMachine> ret = new ArrayList<VirtualMachine>();

    for (VirtualMachine vm : vms) {
      String providerName = System.getProperty("vm." + vm.getName() + ".provider");
      if (providerName == null && !isDockerAvailable()) {
        ret.add(vm);
      }
    }
    return ret;
  }

  private List<VirtualMachine> getJcloudsVms(VirtualMachine ... vms) {
    List <VirtualMachine> ret = new ArrayList<VirtualMachine>();

    for (VirtualMachine vm : vms) {
      String providerName = System.getProperty("vm." + vm.getName() + ".provider");
      if (providerName != null) {
        ret.add(vm);
      }
    }
    return ret;
  }

  private List<VirtualMachine> getDockerVms(VirtualMachine ... vms) {
    List <VirtualMachine> ret = new ArrayList<VirtualMachine>();

    for (VirtualMachine vm : vms) {
      String providerName = System.getProperty("vm." + vm.getName() + ".provider");
      if (providerName == null && isDockerAvailable()) {
        ret.add(vm);
      }
    }

    return ret;
  }

  boolean isDockerAvailable() {
    return true;
  }
}
