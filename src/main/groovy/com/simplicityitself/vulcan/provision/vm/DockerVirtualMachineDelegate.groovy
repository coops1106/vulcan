package com.simplicityitself.vulcan.provision.vm

import com.simplicityitself.vulcan.VirtualMachine
import groovy.util.logging.Slf4j

@Slf4j
class DockerVirtualMachineDelegate implements VirtualMachineDelegate {

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
    return "root"
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
  boolean isRunning() {
    return true;
  }

  @Override
  String getProviderName() {
    return "virtualbox"
  }

  @Override
  void delete() {
    log.info "delete() called for VM $ipAddress"
  }

  @Override
  String getIdentifier() {
    return "vagrant-$ipAddressToConnectTo"
  }
}
