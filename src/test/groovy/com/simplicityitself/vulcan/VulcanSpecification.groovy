package com.simplicityitself.vulcan

import com.google.common.collect.ImmutableSet
import org.jclouds.ContextBuilder
import org.jclouds.aws.ec2.AWSEC2Client
import org.jclouds.compute.ComputeServiceContext
import org.jclouds.compute.domain.Image
import org.jclouds.ec2.domain.Tag
import org.jclouds.ec2.options.DescribeImagesOptions
import org.jclouds.ec2.util.TagFilterBuilder
import org.jclouds.enterprise.config.EnterpriseConfigurationModule
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule
import org.jclouds.sshj.config.SshjSshClientModule


AWSEC2Client client = ContextBuilder.newBuilder("aws-ec2")
        .modules(ImmutableSet.of(new SshjSshClientModule(), new EnterpriseConfigurationModule(), new SLF4JLoggingModule()))
        .credentials("AKIAJKXWFXX6GCR3QH6A", "FQzI8dpqeaKxdcqv/304rBvPshn83EKqAkyy2mKM")
//        .overrides(overrides)
        .buildApi(AWSEC2Client)


//client.getAMIServices().describeImagesInRegion()

def options = DescribeImagesOptions.Builder.imageIds("ami-e03f4289")


println "TAGS == ${client.getTagApiForRegion("us-east-1").get().list()}"

println """TAGS == ${client.getTagApiForRegion("us-east-1").get().filter(
        new TagFilterBuilder().image().resourceId("ami-e03f4289").build())}"""

//def images = client.AMIServices.describeImagesInRegion("us-east-1", options)

//client.tagApi.get().applyToResources(["wibble":"monkey"], ["ami-e03f4289"])

//ComputeServiceContext context = ContextBuilder.newBuilder("aws-ec2")
//        .modules(ImmutableSet.of(new SshjSshClientModule(), new EnterpriseConfigurationModule(), new SLF4JLoggingModule()))
//        .credentials("AKIAJKXWFXX6GCR3QH6A", "FQzI8dpqeaKxdcqv/304rBvPshn83EKqAkyy2mKM")
//        .buildView(ComputeServiceContext)
//
//Image img = context.computeService.getImage("us-east-1/ami-e03f4289")

//println "TAGS ARE ${img.tags}"
