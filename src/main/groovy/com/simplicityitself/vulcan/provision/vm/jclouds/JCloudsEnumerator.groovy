package com.simplicityitself.vulcan.provision.vm.jclouds

import com.simplicityitself.vulcan.VirtualMachine
import org.jclouds.compute.domain.NodeMetadata
import org.jclouds.compute.domain.ComputeMetadata
import org.jclouds.compute.ComputeService
import com.google.common.base.Predicate

/**
 * Lists all the VirtualMachines in a particular provider
 * Not given enough thought, explore the idea more and figure out how to implement this with vagrant too.
 */
class JCloudsEnumerator {

  String group

  List<VirtualMachine> getAllVirtualMachines() {
    throw new IllegalStateException("Badly conceived and written, cleanup....")
    ComputeService computeService = JCloudsConnector.getContext("default").computeService

    Collection<ComputeMetadata> metadata = computeService.listNodesDetailsMatching(new Predicate<ComputeMetadata>() {
      @Override
      boolean apply(ComputeMetadata input) {
        group == computeService.getNodeMetadata(input.id).group
      }
    })

    metadata.collect {
      VirtualMachine vm = new VirtualMachine()
      vm.delegate = new JCloudsVirtualMachineDelegate(
          computeService.getNodeMetadata(it.id), null, computeService)
      return vm
    }
  }

  void removeAllVirtualMachinesInGroup(String group) {
    ComputeService computeService = JCloudsConnector.getContext("default").computeService

    def location = System.properties["vm.default.location"]

    println "Default location for cleanup set to ${location}"

    Collection<ComputeMetadata> metadata = computeService.destroyNodesMatching(new Predicate<NodeMetadata>() {
      @Override
      boolean apply(NodeMetadata input) {
        if (location) {
          return group == input.group && location == input.location
        }
        return group == input.group
      }
    })
  }

}
