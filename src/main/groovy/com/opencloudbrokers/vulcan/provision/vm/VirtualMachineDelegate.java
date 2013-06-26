package com.opencloudbrokers.vulcan.provision.vm;

import com.opencloudbrokers.vulcan.VirtualMachine;

import java.io.File;

public interface VirtualMachineDelegate {
  VirtualMachine.CommandReturn executeCommandAndReturn(String command);

  String getUserName();
  void setUserName(String userName);
  File getSshPrivateKey();

  String getIpAddressToConnectTo();
  int getPortToConnectTo();
  boolean isRunning();
  String getProviderName();
  void delete();
  String getIdentifier();
}
