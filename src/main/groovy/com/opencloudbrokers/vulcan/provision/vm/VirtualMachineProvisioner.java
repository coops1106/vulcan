package com.opencloudbrokers.vulcan.provision.vm;

import com.opencloudbrokers.vulcan.VirtualMachine;

import java.util.List;

public interface VirtualMachineProvisioner {
  void provision(List<VirtualMachine> virtualMachines);
  void disconnect();
}
