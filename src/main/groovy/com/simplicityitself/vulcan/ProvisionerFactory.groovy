package com.simplicityitself.vulcan

import com.simplicityitself.vulcan.provision.vm.jclouds.JCloudsProvision
import com.simplicityitself.vulcan.provision.vm.vagrant.VagrantVirtualMachineProvision
import com.simplicityitself.vulcan.provision.vm.VirtualMachineProvisioner
import groovy.util.logging.Slf4j

@Slf4j
class ProvisionerFactory {

  static VirtualMachineProvisioner selectProvisioner(String name) {
    log.warn("Using default name for infrastructure. boo")

    if (System.properties["vulcan.remote"] != null) {
      log.info "Selected JClouds Provisioner to setup remote infrastructure"
      return new JCloudsProvision(name)
    }
    log.info "Selected Vagrant Provisioner to setup local infrastructure"
    return new VagrantVirtualMachineProvision(name)
  }
}
