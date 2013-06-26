package com.opencloudbrokers.vulcan.provision.cloudfoundry;

import com.opencloudbrokers.vulcan.CloudFoundry;


public interface CloudFoundryProvisioner {

  void provision(CloudFoundry cloudFoundry);
  void disconnect();

}
