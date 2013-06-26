package com.opencloudbrokers.vulcan.test

import com.opencloudbrokers.vulcan.provision.cloudfoundry.CloudApp
import com.opencloudbrokers.vulcan.CloudFoundry
import org.cloudfoundry.client.lib.domain.CloudApplication
import org.cloudfoundry.client.lib.CloudFoundryException
import org.springframework.http.HttpStatus

public class CloudFoundryChecks {

  public static boolean appRunning(CloudApp app, CloudFoundry cloudFoundry) {
    cloudFoundry.client.getApplication(app.name).state == CloudApplication.AppState.STARTED
  }

  public static boolean appDeployed(CloudApp app, CloudFoundry cloudFoundry) {
    try {
      cloudFoundry.client.getApplication(app.name)
      return true
    } catch (CloudFoundryException ex) {
      if (ex.statusCode == HttpStatus.NOT_FOUND) {
        return false
      }
      throw ex
    }
  }
  public static String getFromHttp(CloudApp app, CloudFoundry cloudFoundry, String url) {
    return new URL("http://${cloudFoundry.client.getApplication(app.name).getUris()[0]}${url}").text
  }
}
