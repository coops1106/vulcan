package com.opencloudbrokers.vulcan;

import com.opencloudbrokers.vulcan.provision.cloudfoundry.CloudFoundryProvisioner;
import com.opencloudbrokers.vulcan.provision.cloudfoundry.RemoteCloudFoundryProvisioner;
import com.opencloudbrokers.vulcan.provision.vm.JCloudsEnumerator;
import com.opencloudbrokers.vulcan.provision.vm.JCloudsProvision;
import com.opencloudbrokers.vulcan.provision.vm.VagrantVirtualMachineProvision;
import com.opencloudbrokers.vulcan.provision.vm.VirtualMachineProvisioner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Vulcan {

  private List<VirtualMachineProvisioner> virtualMachineProvisioners = new ArrayList<VirtualMachineProvisioner>();
  private List<CloudFoundryProvisioner> cloudFoundryProvisioners = new ArrayList<CloudFoundryProvisioner>();

  private VagrantVirtualMachineProvision vagrantProvision;

  public static Vulcan withDefaultSettings() {
    Vulcan vulcan = new Vulcan();
    return vulcan;
  }

  public Vulcan initialiseCloudFoundries(CloudFoundry ... cloudFoundries) {

    for (CloudFoundry cloudFoundry: cloudFoundries) {
      provision(cloudFoundry);
    }

    return this;
  }

  public Vulcan initialiseInfrastructure(String infraName, VirtualMachine ... virtualMachines) {

    List<VirtualMachine> vagrantVMs = new ArrayList<VirtualMachine>();
    List<VirtualMachine> jcloudsVMs = new ArrayList<VirtualMachine>();

    for (VirtualMachine vm : virtualMachines) {
      String providerName = System.getProperty("vm." + vm.getName() + ".provider");
      if (providerName == null) {
        vagrantVMs.add(vm);
      } else {
        jcloudsVMs.add(vm);
      }
    }

    VirtualMachineProvisioner jCloudsProvision = new JCloudsProvision(infraName);
    virtualMachineProvisioners.add(jCloudsProvision);

    if (vagrantVMs.size() > 0) {
      vagrantProvision = new VagrantVirtualMachineProvision(infraName);
      virtualMachineProvisioners.add(vagrantProvision);
      vagrantProvision.provision(vagrantVMs);
    }

    jCloudsProvision.provision(jcloudsVMs);

    //at this point, we have our virtual machines.
    return this;
  }

  public VagrantVirtualMachineProvision getVagrantProvision() {
    return vagrantProvision;
  }

  void provision(CloudFoundry cloudFoundry) {

    //select foundry provisioner
    CloudFoundryProvisioner provisioner = new RemoteCloudFoundryProvisioner();
    cloudFoundryProvisioners.add(provisioner);

    //provision the foundry
    provisioner.provision(cloudFoundry);

  }

  public void disconnect() {
    //We should be eating exceptions here to give all resources a chance to clean up...
    for (VirtualMachineProvisioner virtualMachineProvisioner : virtualMachineProvisioners) {
      virtualMachineProvisioner.disconnect();
    }
    for (CloudFoundryProvisioner cloudFoundryProvisioner: cloudFoundryProvisioners) {
      cloudFoundryProvisioner.disconnect();
    }
  }

  public List<VirtualMachine> getAllVirtualMachines() {
//    if (provider.toLowerCase().trim() == "vagrant") {
//      return Collections.emptyList();
//    }
    return new JCloudsEnumerator().getAllVirtualMachines();
  }
}
