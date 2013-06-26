package com.opencloudbrokers.vulcan.provision.cloudfoundry;

import com.opencloudbrokers.vulcan.CloudFoundry;
import com.opencloudbrokers.vulcan.VulcanException;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Connects to a remote cloud foundry and implements the common interface over it.
 *
 * Required System Properties :-
 *  cloudfoundry.[CloudFoundry.name].email
 *  cloudfoundry.[CloudFoundry.name].api
 *  cloudfoundry.[CloudFoundry.name].password
 */
public class RemoteCloudFoundryProvisioner implements CloudFoundryProvisioner {

  Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public void provision(CloudFoundry cloudFoundry) {
    try {
      //select the correct URL and login details from config (sys prop... config file?)
      String email = System.getProperty("cloudfoundry." + cloudFoundry.getName() + ".email");
      String password = System.getProperty("cloudfoundry." + cloudFoundry.getName() + ".password");
      String apiUrl = System.getProperty("cloudfoundry." + cloudFoundry.getName() + ".api");

      if (email == null || password == null || apiUrl == null) {
        throw new IllegalStateException("Attempting to use a remote Cloud Foundry, " +
            "but system properties cloudfoundry." + cloudFoundry.getName() + ".email and " +
            "cloudfoundry." + cloudFoundry.getName() + ".password and " +
            "cloudfoundry." + cloudFoundry.getName() + ".api are not all set");
      }
      log.info("Connecting to " + apiUrl);
      log.info("With email " + email);

      CloudCredentials credentials = new CloudCredentials(email, password);
      URL cloudControllerUrl = new URL(apiUrl);

      CloudFoundryClient client = new CloudFoundryClient(credentials, cloudControllerUrl);

      cloudFoundry.setClient(client);
    } catch (MalformedURLException e) {
      throw new VulcanException("Cannot connect to remote VM", e);
    }
  }

  @Override
  public void disconnect() {

  }
}
