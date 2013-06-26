package com.opencloudbrokers.vulcan;


import com.opencloudbrokers.vulcan.data.CloudFoundryServiceBackup;
import com.opencloudbrokers.vulcan.provision.cloudfoundry.CloudApp;
import com.opencloudbrokers.vulcan.test.CloudFoundryChecks;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;

public class CloudFoundry {

  private static Logger log = LoggerFactory.getLogger(CloudFoundry.class);

  private CloudFoundryClient client;
  private String name;
  private String baseUrl;

  public CloudFoundry(String name) {
    this.name = name;
  }

  //TODO, this shouldn't be here, need to detect or set the base URL using properties.
  public CloudFoundry(String name, String url) {
    this.name = name;
    baseUrl = url;
  }

  public String getName() {
    return name;
  }

  public void setClient(CloudFoundryClient client) {
    this.client = client;
    if (baseUrl == null) {
      String url = client.getCloudControllerUrl().toString();
      baseUrl = url.substring(url.indexOf("."));
      log.debug("Setting application base URL top " + baseUrl);
    }
    client.login();
  }

  public CloudFoundryClient getClient() {
    return client;
  }

  public void createOrUpdate(CloudApp app) throws IOException {
    String appUrl = getAppUrl(app.name);

    log.info("Deploying application to " + appUrl);
    //TODO erm, this has created a circular dep between the two classes that ideally wouldn't exist.
    //Consider that there may be a concept missing. probably this class blends two things together.
    if (!CloudFoundryChecks.appDeployed(app, this)) {
      client.createApplication(app.name, app.framework, app.memory,
          Arrays.asList(appUrl), null);
    } else {
//      updateToMatchDescriptor(app);
      log.warn("Application already exists on the CloudFoundry, service/ attribute convergence is not yet implemented.");
    }

    client.uploadApplication(app.name, app.location);
  }

  private String getAppUrl(String name) {

    return "http://" + name + baseUrl;
  }

  public void restoreServices(CloudApp app, CloudFoundryServiceBackup backup) {
    checkDelegate();
//    throw new IllegalStateException("not implemented");
  }

  public CloudFoundryServiceBackup backupServices(CloudApp app) {
    checkDelegate();
//    throw new IllegalStateException("not implemented");
    return null;
  }

  public void start(CloudApp app) {
    checkDelegate();
    client.startApplication(app.name);
  }

  public void destroy(CloudApp app) {
    checkDelegate();
    client.deleteApplication(app.name);
  }

  private void checkDelegate() {
    if (client == null) {
      throw new IllegalStateException("This Cloud Foundry connection has not been initialised.  Call Vulcan.initialiseCloudFoundries with this as a parameter to connect it to a running Cloud Foundry");
    }
  }
}
