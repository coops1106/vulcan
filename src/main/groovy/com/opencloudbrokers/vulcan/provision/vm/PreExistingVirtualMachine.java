package com.opencloudbrokers.vulcan.provision.vm;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.opencloudbrokers.vulcan.VirtualMachine;
import com.opencloudbrokers.vulcan.VulcanException;
import com.opencloudbrokers.vulcan.provision.vm.SSHUtils;

import java.io.File;

/**
 * Use if you want to connect to existing VMs and perform normal assertions against them..
 */
public class PreExistingVirtualMachine implements VirtualMachineDelegate {

  private String username;
  private File sshPrivateKey;
  private String hostname;
  private JSch ssh;
  private Session sshSession;

  public PreExistingVirtualMachine(String hostname, String username, File sshPrivateKey) {
    this.username = username;
    this.hostname = hostname;
    this.sshPrivateKey = sshPrivateKey;
    setupSsh();
  }

  private void setupSsh() {

    try {
      ssh = new JSch();
      ssh.addIdentity(sshPrivateKey.getAbsolutePath());
      sshSession = ssh.getSession(username, hostname, 22);
      sshSession.setConfig("StrictHostKeyChecking", "no");
      sshSession.connect(2000);

    } catch (JSchException e) {
      throw new VulcanException("Unable to connect to existing VM at " + hostname + " as user " + username, e);
    }
  }

  @Override
  public VirtualMachine.CommandReturn executeCommandAndReturn(String command) throws VulcanException {
    return SSHUtils.executeCommand(command, this);
  }

  @Override
  public void setUserName(String userName) {
    username=userName;
  }

  @Override
  public String getUserName() {
    return username;
  }

  @Override
  public File getSshPrivateKey() {
    return sshPrivateKey;
  }

  @Override
  public String getIpAddressToConnectTo() {
    return hostname;
  }

  @Override
  public int getPortToConnectTo() {
    return 22;
  }

  @Override
  public boolean isRunning() {
    return true;
  }

  @Override
  public String getProviderName() {
    return "pre-existing";
  }

  @Override
  public void delete() {
    throw new IllegalStateException("Pre existing VM cannot be removed");
  }

  @Override
  public String getIdentifier() {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }
}
