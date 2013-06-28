package com.simplicityitself.vulcan

import com.simplicityitself.vulcan.provision.vm.VirtualMachineDelegate

import java.io.File
import java.util.List

/**
 * Represents a VM.  You declare it and then Vulcan will provision a delegate that sits behind it.
 * Once that has occurred, you may use instances of this class to interact with Vms and perform assertions against them
 *
 * Implementations provide transparency around the network conditions, abstracting the behaviour
 * between different providers network setups.
 */
public class VirtualMachine {
  VirtualMachineImage image
  String name
  String os
  int memory
  int cpuCount
  int disk
  String group
  List<Integer> openPorts
  private VirtualMachineDelegate delegate

  public void setUserName(String username) {
    checkDelegate()
    delegate.setUserName(username)
  }

  public String getProviderName() {
    checkDelegate()
    return delegate.getProviderName()
  }

  public CommandReturn executeCommandAndReturn(String command) {
    checkDelegate()
    return delegate.executeCommandAndReturn(command)
  }
  public String getUserName() {
    checkDelegate()
    return delegate.getUserName()
  }
  public File getSshPrivateKey() {
    checkDelegate()
    return delegate.getSshPrivateKey()
  }
  public String getIpAddressToConnectTo() {
    checkDelegate()
    return delegate.getIpAddressToConnectTo()
  }
  public int getPortToConnectTo() {
    checkDelegate()
    return delegate.getPortToConnectTo()
  }

  public String getIdentifier() {
    checkDelegate()
    return delegate.getIdentifier()
  }

  private void checkDelegate() {
    if (delegate == null) {
      throw new IllegalStateException("This VirtualMachine has not been initialised.  Call Vulcan.initialiseInfrastructure with this as a parameter to connect it to a running virtual machine")
    }
  }

  void setDelegate(VirtualMachineDelegate delegate) {
    this.delegate = delegate
  }

  public static class CommandReturn {
    private String sysError
    private String sysOutput
    private int returnCode

    public CommandReturn(String sysError, String sysOutput, int returnCode) {
      this.sysError = sysError
      this.sysOutput = sysOutput
      this.returnCode = returnCode
    }

    public String getSysError() {
      return sysError
    }

    public String getSysOutput() {
      return sysOutput
    }

    public int getReturnCode() {
      return returnCode
    }
  }
}
