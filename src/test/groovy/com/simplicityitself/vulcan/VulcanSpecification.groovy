package com.simplicityitself.vulcan

import com.simplicityitself.vulcan.deploy.SimpleDeployer
import com.simplicityitself.vulcan.spock.VulcanTrade
import com.simplicityitself.vulcan.test.InfrastructureChecks
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

/**
 * Generate some example infrastructure
 */
@Mixin([SimpleDeployer, InfrastructureChecks])
@VulcanTrade
class VulcanSpecification extends Specification {

  @Shared @AutoCleanup("disconnect")
  Vulcan vulcan = Vulcan.withDefaultSettings("ExampleInfrastructure")

  @Shared VirtualMachine myServer
  @Shared VirtualMachine myOtherServer

  def "initialise the infrastructure"() {
    given:
    myServer = new VirtualMachine(
            group:"myservers",
            name:"zipServer",
            os:"ubuntu",
            memory:512,
            cpuCount:1,
            disk:8, openPorts:[22, 27017])

    myOtherServer = new VirtualMachine(
            group:"myservers",
            name:"apache",
            os:"ubuntu",
            memory:512,
            cpuCount:1,
            disk:8, openPorts:[22, 27017])

    when:
    vulcan.initialise(myServer, myOtherServer)

    then:
    vulcan.provisioner.isRunning(myServer)
    vulcan.provisioner.isRunning(myOtherServer)
  }

  def "install apache"() {
    when:
    exec(myOtherServer, "sudo apt-get update")
    exec(myOtherServer, "sudo apt-get -y install apache2.2")

    then:
    tryFor5min {
      portOpen(myOtherServer, 80)
    }
  }

  def "install zip"() {
    when:
    exec(myOtherServer, "sudo apt-get -y install unzip")

    then:
    commandAvailable(myOtherServer, "unzip")
  }
}
