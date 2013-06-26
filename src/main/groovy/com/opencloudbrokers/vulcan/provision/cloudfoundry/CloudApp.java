package com.opencloudbrokers.vulcan.provision.cloudfoundry;

import java.io.File;

public class CloudApp {
  public String name;
  public String framework;
  public int memory;
  public int instances;
  public File location;
  public String[] domainNames;
}