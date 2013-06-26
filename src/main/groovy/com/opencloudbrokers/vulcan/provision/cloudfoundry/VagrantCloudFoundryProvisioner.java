package com.opencloudbrokers.vulcan.provision.cloudfoundry;

import com.opencloudbrokers.vulcan.CloudFoundry;

/**
 * Boots a vagrant backed micro cloud foundry and presents the common interface to it.
 */
public class VagrantCloudFoundryProvisioner implements CloudFoundryProvisioner {

  @Override
  public void provision(CloudFoundry cloudFoundry) {
    //choose a domain..

    //boot a micro cloud foundry instance
    //run the alter script with the domain we have chosen

    //TODO, snapshot this?


  }

  @Override
  public void disconnect() {
    //To change body of implemented methods use File | Settings | File Templates.
  }
}
