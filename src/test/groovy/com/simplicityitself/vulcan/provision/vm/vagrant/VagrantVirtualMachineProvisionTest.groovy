package com.simplicityitself.vulcan.provision.vm.vagrant

import spock.lang.Specification

class VagrantVirtualMachineProvisionTest extends Specification {

  def VagrantVirtualMachineProvision uut

  def setup() {
    uut = new VagrantVirtualMachineProvision("test")
  }

  def "given my OS when I determineOS then I know if my OS is windows" () {
    given:
    def ant = new AntBuilder()
    ant.condition(property:"winOS"){
      os(family:"windows")
    }
    def osIsWindows = ant.project.getProperties().get("winOS") != null

    when:
    uut.determineOS()

    then:
    uut.osIsWindows == osIsWindows
  }

  def "I can get the location of the vagrant bat file"() {
    when:
    String vagrantBatDir = uut.getVagrantBatDir()

    then:
    vagrantBatDir != null
    println(vagrantBatDir)

  }

  def "I can download a basebox" () {
    given:
    uut.determineOS()
    uut.setupSpecificationDir("ExampleInfrastructure")

    when:
    uut.downloadBaseBox("vulcan", "http://dl.dropbox.com/u/1537815/precise64.box")

    then:
    notThrown(IllegalStateException)
  }
}
