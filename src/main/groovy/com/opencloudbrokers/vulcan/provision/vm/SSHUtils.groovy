package com.opencloudbrokers.vulcan.provision.vm

import com.opencloudbrokers.vulcan.VirtualMachine.CommandReturn
import com.opencloudbrokers.vulcan.VirtualMachine
import com.opencloudbrokers.vulcan.provision.vm.VirtualMachineDelegate

class SSHUtils {

  static boolean copyFileFrom(String remoteFile, File localDir, VirtualMachine vm) {
    def ant = new AntBuilder()
    localDir.mkdirs()
    ant.scp(
            port: vm.portToConnectTo,
            keyFile: vm.sshPrivateKey,
            trust: "yes",
            remoteFile:"${vm.userName}@${vm.ipAddressToConnectTo}:${remoteFile}",
            localTodir:localDir.absolutePath
    )
    return true
  }
  static boolean copyFileTo(File from, String remoteDir, VirtualMachine vm) {
    def ant = new AntBuilder()
    ant.scp(
            port: vm.portToConnectTo,
            keyFile: vm.sshPrivateKey,
            trust: "yes",
            file:from.absolutePath,
            remoteTofile:"${vm.userName}@${vm.ipAddressToConnectTo}:${remoteDir}",
    )
    return true
  }

  static boolean migrate(String remoteFile, VirtualMachine source, VirtualMachine target) {
    //TODO somehow handle network strangeness..... (if one can't see the other, proxy via local)
    executeCommand("scp ${remoteFile} ${target.userName}@${target.ipAddressToConnectTo}:${remoteFile}", source.delegate)
    return true
  }

  static CommandReturn executeCommand(def command, VirtualMachineDelegate vm) {
    //TODO, don't use the ant task...
    //would still be desirable to echo the output to the console.
    //also allow stream monitioring for error monitors to act on.
    def file = File.createTempFile("vulcanssh", "sshout")

    new AntBuilder().sshexec(host: vm.ipAddressToConnectTo,

        port: vm.portToConnectTo,
        username: vm.userName,
        keyFile: vm.sshPrivateKey,
        trust: "yes",
        output:file,
        command: command,
        failOnError:false)

    return new CommandReturn("Not implemented", file.text, 0)
  }
}
