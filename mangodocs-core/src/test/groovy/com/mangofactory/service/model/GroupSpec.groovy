package com.mangofactory.service.model

import com.mangofactory.documentation.service.model.ApiKey
import com.mangofactory.documentation.service.model.ApiListingReference
import com.mangofactory.documentation.service.model.AuthorizationType
import com.mangofactory.documentation.service.model.Group
import com.mangofactory.documentation.builders.GroupBuilder
import com.mangofactory.documentation.builders.ResourceListingBuilder
import spock.lang.Specification

import static com.google.common.collect.Maps.newHashMap

class GroupSpec extends Specification {
  def "Groups are built correctly" () {
    given:
      List<AuthorizationType> authorizations = [new ApiKey("test", "header")]
      Group built = new GroupBuilder()
              .resourceListing(new ResourceListingBuilder()
                .authorizations(authorizations)
                .apis([Mock(ApiListingReference)])
                .build())
              .apiListingsByGroup(newHashMap())
              .build()
    expect:
      built.apiListings.size() == 0
      built.resourceListing.authorizations.size() == 1
  }
}
