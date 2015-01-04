package com.mangofactory.service.model

import com.mangofactory.service.model.builder.GroupBuilder
import com.mangofactory.service.model.builder.ResourceListingBuilder
import spock.lang.Specification

import static com.google.common.collect.Maps.newHashMap

class GroupSpec extends Specification {
  def "Groups are built correctly" () {
    given:
      List<AuthorizationType> authorizations = [new ApiKey("test", "header")]
      Group built = new GroupBuilder()
              .withResourceListing(new ResourceListingBuilder()
                .authorizations(authorizations)
                .apis([Mock(ApiListingReference)])
                .build())
              .withApiListings(newHashMap())
              .build()
    expect:
      built.apiListings.size() == 0
      built.resourceListing.authorizations.size() == 1
  }
}
