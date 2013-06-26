package com.opencloudbrokers.vulcan.data

import com.opencloudbrokers.vulcan.provision.cloudfoundry.CloudApp
import com.opencloudbrokers.vulcan.CloudFoundry

/**
 * Descriptor of a service backup.
 * Describes the backups, where they are and some data that can be used to validate a correct
 * restore.
 */
class CloudFoundryServiceBackup {

  PostgresBackup backup

  CloudApp app

  CloudFoundryServiceBackup(CloudApp app) {
    this.app = app
  }

  void backupFrom(CloudFoundry foundry) {
    backup = new PostgresBackup()
    connectPostgres()

    //TODO, backup the database with pg_backup.
  }

  //TODO, get the size of the database...
  //TODO some way to figure out the cost of the backup run to include in pricing calculations

  void connectPostgres() {
    //TODO, ensure that PostgresBackup has the client tools necessary to run

    //TODO ensure caldecott is installed
    //TODO, establish a tunnel to postgres
    //TODO, perform a connection check
  }

  void restoreTo(CloudFoundry foundry) {
    connectPostgres()

    //TODO, assert that a backup exists.
    //TODO, call pg_restore
  }
}
