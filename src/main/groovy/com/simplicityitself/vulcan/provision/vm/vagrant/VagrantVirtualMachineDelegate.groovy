package com.simplicityitself.vulcan.provision.vm.vagrant

import com.simplicityitself.vulcan.VirtualMachine
import com.simplicityitself.vulcan.provision.vm.SSHUtils
import com.simplicityitself.vulcan.provision.vm.VirtualMachineDelegate
import groovy.util.logging.Slf4j

@Slf4j
class VagrantVirtualMachineDelegate implements VirtualMachineDelegate {

  String ipAddress
  File privateKey
  String overrideUsername

  @Override
  VirtualMachine.CommandReturn executeCommandAndReturn(String command) {
    return SSHUtils.executeCommand(command, this)
  }

  @Override
  void setUserName(String userName) {
    overrideUsername = userName
  }

  @Override
  public String getUserName() {
    if (overrideUsername) {
      return overrideUsername;
    }
    return "vagrant"
  }

  @Override
  File getSshPrivateKey() {
    return privateKey
  }

  @Override
  String getIpAddressToConnectTo() {
    return ipAddress
  }

  @Override
  int getPortToConnectTo() {
    return 22
  }

  @Override
  String getProviderName() {
    return "virtualbox"
  }

  @Override
  String getIdentifier() {
    return "vagrant-$ipAddressToConnectTo"
  }
}
