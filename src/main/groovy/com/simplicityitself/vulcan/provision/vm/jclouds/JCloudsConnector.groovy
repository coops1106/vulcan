package com.simplicityitself.vulcan.provision.vm.jclouds

import org.jclouds.aws.ec2.AWSEC2Client
import org.jclouds.aws.ec2.reference.AWSEC2Constants
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
    def identity   = getIdentity(providerId)
    def credential = getCredential(providerId)

    println "Have vmName = $vmName"
    println "Have providerId = $providerId"
    println "Have identity = ${identity != null && identity.length() > 0}"
    println "Have credential = ${credential != null && credential.length() > 0}"

    Properties overrides = new Properties();

    // choose only amazon images that are ebs-backed
    overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY,
            "state=available;image-type=machine;root-device-type=ebs");

    return ContextBuilder.newBuilder(providerId)
              .modules(ImmutableSet.of(new SshjSshClientModule(), new EnterpriseConfigurationModule(), new SLF4JLoggingModule()))
              .credentials(identity, credential)
              .overrides(overrides)
              .buildView(ComputeServiceContext)

  }

  static def getProviderView(String providerId, Class type) {
    def identity   = getIdentity(providerId)
    def credential = getCredential(providerId)

    println "Have providerId = $providerId"
    println "Have identity = ${identity != null && identity.length() > 0}"
    println "Have credential = ${credential != null && credential.length() > 0}"

    Properties overrides = new Properties();

    // choose only amazon images that are ebs-backed
    overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY,
            "owner-alias=amazon,self;state=available;image-type=machine;root-device-type=ebs");

    return ContextBuilder.newBuilder(providerId)
            .modules(ImmutableSet.of(new SshjSshClientModule(), new EnterpriseConfigurationModule(), new SLF4JLoggingModule()))
            .credentials(identity, credential)
            .overrides(overrides)
            .buildView(type)
  }

  static <D> D getProviderApi(String providerId, Class<D> type) {
    def identity   = getIdentity(providerId)
    def credential = getCredential(providerId)

    println "Have providerId = $providerId"
    println "Have identity = ${identity != null && identity.length() > 0}"
    println "Have credential = ${credential != null && credential.length() > 0}"

    Properties overrides = new Properties();

    // choose only amazon images that are ebs-backed
    overrides.setProperty(AWSEC2Constants.PROPERTY_EC2_AMI_QUERY,
            "state=available;image-type=machine;root-device-type=ebs");

    return ContextBuilder.newBuilder(providerId)
            .modules(ImmutableSet.of(new SshjSshClientModule(), new EnterpriseConfigurationModule(), new SLF4JLoggingModule()))
            .credentials(identity, credential)
            .overrides(overrides)
            .buildApi(type)

  }

  //only using system props.
  static String getProvider(def vmName) {
    System.getProperty("vm.${vmName}.provider")
  }

  //currently getting these from env vars
  static def getIdentity(def providerId) {
    System.getenv("VULCAN_IDENTITY_" + providerId)
  }

  static def getCredential(def providerId) {
    System.getenv("VULCAN_CREDENTIAL_" + providerId)
  }
}
