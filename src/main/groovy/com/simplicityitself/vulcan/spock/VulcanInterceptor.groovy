package com.simplicityitself.vulcan.spock

import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.model.ErrorInfo

class VulcanInterceptor extends AbstractRunListener {
  boolean broken = false

  @Override
  void error(ErrorInfo error) {
    broken=true
    error.method.feature?.parent?.features*.skipped=true
  }
}
