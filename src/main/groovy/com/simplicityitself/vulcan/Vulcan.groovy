package com.simplicityitself.vulcan;

import com.simplicityitself.vulcan.provision.vm.*;

import java.util.ArrayList;
import java.util.List;

public class Vulcan {

  VirtualMachineProvisioner provisioner

  public static Vulcan withDefaultSettings(String name) {
    Vulcan vulcan = new Vulcan();
    vulcan.provisioner = ProvisionerFactory.selectProvisioner(name)
    return vulcan;
  }

  public Vulcan initialise(VirtualMachine ... virtualMachines) {
    provisioner.provision(virtualMachines as List)
    return this;
  }

  public void disconnect() {
    provisioner.disconnect()
  }
}
