package com.opencloudbrokers.vulcan;

import com.opencloudbrokers.vulcan.provision.vm.VirtualMachineDelegate;

import java.io.File;
import java.util.List;

/**
 * Represents a VM.  You declare it and then Vulcan will provision a delegate that sits behind it.
 * Once that has occurred, you may use instances of this class to interact with Vms and perform assertions against them
 *
 * Implementations provide transparency around the network conditions, abstracting the behaviour
 * between different providers network setups.
 */
public class VirtualMachine {
  private String name;
  private String os;
  private int memory;
  private int cpuCount;
  private int disk;
  private List<Integer> ports;
  private VirtualMachineDelegate delegate;

  public void setUserName(String username) {
    checkDelegate();
    delegate.setUserName(username);
  }

  public List<Integer> getOpenPorts() {
    return ports;
  }

  public void setOpenPorts(List<Integer> ports) {
    this.ports = ports;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getOs() {
    return os;
  }

  public void setOs(String os) {
    this.os = os;
  }

  public int getMemory() {
    return memory;
  }

  public void setMemory(int memory) {
    this.memory = memory;
  }

  public int getCpuCount() {
    return cpuCount;
  }

  public void setCpuCount(int cpuCount) {
    this.cpuCount = cpuCount;
  }

  public int getDisk() {
    return disk;
  }

  public void setDisk(int disk) {
    this.disk = disk;
  }

  public String getProviderName() {
    checkDelegate();
    return delegate.getProviderName();
  }

  public CommandReturn executeCommandAndReturn(String command) {
    checkDelegate();
    return delegate.executeCommandAndReturn(command);
  }
  public String getUserName() {
    checkDelegate();
    return delegate.getUserName();
  }
  public File getSshPrivateKey() {
    checkDelegate();
    return delegate.getSshPrivateKey();
  }
  public String getIpAddressToConnectTo() {
    checkDelegate();
    return delegate.getIpAddressToConnectTo();
  }
  public int getPortToConnectTo() {
    checkDelegate();
    return delegate.getPortToConnectTo();
  }
  public boolean isRunning() {
    checkDelegate();
    return delegate.isRunning();
  }

  public boolean deleteFromProvider() {
    checkDelegate();
    delegate.delete();
    return true;
  }

  public String getIdentifier() {
    checkDelegate();
    return delegate.getIdentifier();
  }

  private void checkDelegate() {
    if (delegate == null) {
      throw new IllegalStateException("This VirtualMachine has not been initialised.  Call Vulcan.initialiseInfrastructure with this as a parameter to connect it to a running virtual machine");
    }
  }

  void setDelegate(VirtualMachineDelegate delegate) {
    this.delegate = delegate;
  }

  public static class CommandReturn {
    private String sysError;
    private String sysOutput;
    private int returnCode;

    public CommandReturn(String sysError, String sysOutput, int returnCode) {
      this.sysError = sysError;
      this.sysOutput = sysOutput;
      this.returnCode = returnCode;
    }

    public String getSysError() {
      return sysError;
    }

    public String getSysOutput() {
      return sysOutput;
    }

    public int getReturnCode() {
      return returnCode;
    }
  }
}
