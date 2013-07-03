package com.simplicityitself.vulcan.provision.vm.jclouds

import com.simplicityitself.vulcan.VirtualMachine
import com.simplicityitself.vulcan.provision.vm.SSHUtils
import com.simplicityitself.vulcan.provision.vm.VirtualMachineDelegate
import groovy.util.logging.Slf4j
import org.jclouds.compute.ComputeService
import org.jclouds.compute.domain.NodeMetadata
import org.jclouds.compute.domain.OsFamily

@Slf4j
class JCloudsVirtualMachineDelegate implements VirtualMachineDelegate {
  private NodeMetadata metaData;
  private File tk
  private File priv
  private String overrideUsername
  final String providerName
  final String identifier

  JCloudsVirtualMachineDelegate(NodeMetadata metaData, File tk, ComputeService computeService, String providerName) {
    this.metaData = metaData;
    this.tk = tk

    this.providerName = providerName
    identifier = metaData.id

    //TODO, this is extracting the private key and then writing it locally, is this secure or wise?
    priv = new File(tk, UUID.randomUUID().toString())
    if (metaData.credentials?.privateKey != null) {
      log.info("Using JClouds provided private key from the provider API")
      priv << this.metaData.credentials.privateKey
    } else {
      log.warn("Using local key, as no key is available from the provider.")
      priv << new File(System.getenv("VULCAN_PRIVATE_KEY")).text
    }
  }

  @Override
  public VirtualMachine.CommandReturn executeCommandAndReturn(String command) {
    return SSHUtils.executeCommand(command, this)
  }

  @Override
  public String getUserName() {
    if (overrideUsername) {
      return overrideUsername;
    }
//    if (metaData.credentials.user == "root" && metaData.operatingSystem.family == OsFamily.UBUNTU) {
    if (metaData.credentials.user == "root") {
      return "ubuntu"
    }
    return metaData.credentials.user
  }

  @Override
  void setUserName(String userName) {
    overrideUsername = userName
  }

  @Override
  public File getSshPrivateKey() {
    return priv
  }

  @Override
  public String getIpAddressToConnectTo() {
    return metaData.getPublicAddresses().iterator().next();
  }

  @Override
  public int getPortToConnectTo() {
    return 22;
  }
}
