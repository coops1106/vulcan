package com.opencloudbrokers.vulcan.spock;

import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;
import org.spockframework.runtime.model.SpecInfo;

public class VulcanExtension extends AbstractAnnotationDrivenExtension<VulcanTrade> {

  @Override
  public void visitSpecAnnotation(VulcanTrade annotation, SpecInfo spec) {
    VulcanInterceptor v = new VulcanInterceptor();
    for (FeatureInfo feature: spec.getAllFeatures()) {
      spec.addListener(v);
    }
  }
}
