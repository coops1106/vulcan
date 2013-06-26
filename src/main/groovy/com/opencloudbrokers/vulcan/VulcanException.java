package com.opencloudbrokers.vulcan;

public class VulcanException extends RuntimeException {

  public VulcanException(String message, Exception cause) {
    super(message, cause);
  }

}
