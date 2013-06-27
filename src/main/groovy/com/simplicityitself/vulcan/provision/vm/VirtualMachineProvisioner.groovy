package com.simplicityitself.vulcan.provision.vm;

import com.simplicityitself.vulcan.VirtualMachine;

import java.util.List;

public interface VirtualMachineProvisioner {
  void provision(List<VirtualMachine> virtualMachines);
  void disconnect();
}
