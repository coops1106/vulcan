package com.simplicityitself.vulcan.provision.vm

import org.jclouds.compute.ComputeServiceContext
import com.google.common.collect.ImmutableSet
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule
import org.jclouds.sshj.config.SshjSshClientModule
import org.jclouds.ContextBuilder
import org.jclouds.enterprise.config.EnterpriseConfigurationModule

/**
 * Opens a connection to the current provider
 */
class JCloudsConnector {

  static ComputeServiceContext getContext(String vmName) {
    def providerId = getProvider(vmName)
    def identity   = getIdentity(vmName)
    def credential = getCredential(vmName)

    println "Have vmName = $vmName"
    println "Have providerId = $providerId"
    println "Have identity = ${identity != null && identity.length() > 0}"
    println "Have credential = ${credential != null && credential.length() > 0}"

    return ContextBuilder.newBuilder(providerId)
              .modules(ImmutableSet.of(new SshjSshClientModule(), new EnterpriseConfigurationModule(), new SLF4JLoggingModule()))
              .credentials(identity, credential)
              .buildView(ComputeServiceContext)

  }

  //only using system props.
  static def getProvider(def vmName) {
    System.getProperty("vm.${vmName}.provider")
  }

  //currently getting these from env vars
  static def getIdentity(def vmName) {
    System.getenv("VULCAN_IDENTITY_" + getProvider(vmName))
  }

  static def getCredential(def vmName) {
    System.getenv("VULCAN_CREDENTIAL_" + getProvider(vmName))
  }
}
