package com.simplicityitself.vulcan.provision.vm;

import com.simplicityitself.vulcan.VirtualMachine;

import java.io.File;

public interface VirtualMachineDelegate {
  VirtualMachine.CommandReturn executeCommandAndReturn(String command);

  String getUserName();
  void setUserName(String userName);
  File getSshPrivateKey();

  String getIpAddressToConnectTo();
  int getPortToConnectTo();
  String getProviderName();
  String getIdentifier();
}
