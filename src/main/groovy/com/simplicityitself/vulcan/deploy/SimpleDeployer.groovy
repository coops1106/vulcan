package com.simplicityitself.vulcan.deploy;

import com.simplicityitself.vulcan.VirtualMachine;
import com.simplicityitself.vulcan.provision.vm.SSHUtils;

import java.io.File;

/**
 * A basic deployment system, Useful for really simple stuff...
 * For anything more than small, use one of the other deployment systems, chef, puppet, pallet et al.
 */
public class SimpleDeployer {

  public static void copyFileTo(VirtualMachine vm, File file, String remoteDir) {
    SSHUtils.copyFileTo(file, remoteDir, vm);
  }
  public static void copyFileFrom(VirtualMachine vm, String remoteFile, File localDir) {
    SSHUtils.copyFileFrom(remoteFile, localDir, vm);
  }
  public static void migrateFile(VirtualMachine source, VirtualMachine target, String remoteFile) {
    SSHUtils.migrate(remoteFile, source, target);
  }
}
