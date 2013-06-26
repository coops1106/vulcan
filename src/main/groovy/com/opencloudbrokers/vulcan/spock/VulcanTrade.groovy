package com.opencloudbrokers.vulcan.spock

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.*

/**
 * A Spock extension annotation.
 * Applies some lifecycle management to the normal flow.
 *
 * Methods are run in source order.
 * If a method fails, all subsequent methods are skipped (to prevent inconsistent state being applied)
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.METHOD, ElementType.TYPE])
@ExtensionAnnotation(VulcanExtension.class)
public @interface VulcanTrade {

}