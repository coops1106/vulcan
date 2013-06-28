package com.simplicityitself.vulcan.provision.vm;

import com.simplicityitself.vulcan.VirtualMachine
import com.simplicityitself.vulcan.VirtualMachineImage;

import java.util.List;

public interface VirtualMachineProvisioner {
  void provision(List<VirtualMachine> virtualMachines);
  void disconnect();
  boolean isRunning(VirtualMachine vm)
  void deleteFromProvider(VirtualMachine vm)
  VirtualMachineImage generateProviderImageFrom(String imageName, VirtualMachine vm)
  void deleteFromProvider(VirtualMachineImage virtualMachineImage)
  VirtualMachineImage getImage(String id)
  VirtualMachineImage addTagToImage(VirtualMachineImage image, String tag)
}
