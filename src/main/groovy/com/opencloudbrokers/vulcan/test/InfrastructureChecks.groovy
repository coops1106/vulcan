package com.opencloudbrokers.vulcan.test;

import com.opencloudbrokers.vulcan.VirtualMachine;


import java.util.concurrent.TimeoutException;

/**
 * Low level infrastructure checks
 * Carried out with HTTP, sockets or SSH (using Jsch)
 *
 * Intended to be wrapped into pretty DSLs
 */
public class InfrastructureChecks {

  public static String commandOutput(VirtualMachine virtualMachine, String command) {
    return virtualMachine.executeCommandAndReturn(command).getSysOutput();
  }

  public static boolean fileExists(VirtualMachine virtualMachine, String filename) {
    return commandOutput(virtualMachine, "ls -d " + filename + " | wc -w").trim().endsWith("1");
  }
  public static boolean processWithNameIsRunning(VirtualMachine virtualMachine, String processName) {
    return !commandOutput(virtualMachine, "ps aux| grep -v \"grep\" | grep \"" + processName + "\" | wc -l").trim().contains("0");
  }
  public static boolean commandAvailable(VirtualMachine virtualMachine, String commandName) {
    return commandOutput(virtualMachine, "which " + commandName).trim().contains(commandName);
  }
  public static boolean portOpen(VirtualMachine virtualMachine, int port) {
    return commandOutput(virtualMachine, "netstat -ntl | grep \":" + port + " \"").length() > 0;
  }
  public static String getFromHttp(VirtualMachine vm, int port, String url) {
    return new URL("http://${vm.ipAddressToConnectTo}:${port}${url}").text
  }
  public static String textFileContents(VirtualMachine vm, String name) {
    return commandOutput(vm, "cat $name");
  }

  public static String md5HashOf(VirtualMachine vm, String filename) {
    return commandOutput(vm, "md5sum $filename").split(" ")[0]
  }
  public static void appendOntoFile(VirtualMachine vm, String value, String file) {
    commandOutput(vm, "sudo touch $file");
    commandOutput(vm, "echo \"$value\" | sudo tee -a $file");
  }

  public static boolean tryFor30s(Closure exec) {
    return tryTillTimeout(30000, exec)
  }
  public static boolean tryFor5min(Closure exec) {
    return tryTillTimeout(1000 * 60 * 5, exec)
  }

  public static boolean tryTillTimeout(int timeout, Closure exec) {
    def ret

    try {
      ret = exec()
    } catch (Exception ex) {
      println "Caught ${ex.message}"
    }

    long started = System.currentTimeMillis()

    while (!ret) {
      checkTimeOut(started, timeout);
      Thread.sleep(500)

      try {
        ret = exec()
      } catch (Exception ex) {
        println "Caught ${ex.message}"
      }
    }
    ret
  }
  static void checkTimeOut(long started, long timeout) {
    if (System.currentTimeMillis() > started + timeout) {
      throw new TimeoutException("Test did not pass within timeout")
    }
  }

  /*
   processInPIDFileIsRunning
   processWithNameIsRunning

   textFile
   */

  //TODO, other stuff...

}

