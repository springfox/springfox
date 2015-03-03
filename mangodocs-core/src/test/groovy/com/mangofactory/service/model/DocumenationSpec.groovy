package com.mangofactory.service.model

import com.mangofactory.documentation.service.ApiKey
import com.mangofactory.documentation.service.ApiListingReference
import com.mangofactory.documentation.service.AuthorizationType
import com.mangofactory.documentation.service.Documentation
import com.mangofactory.documentation.builders.DocumentationBuilder
import com.mangofactory.documentation.builders.ResourceListingBuilder
import spock.lang.Specification

import static com.google.common.collect.Maps.newHashMap

class DocumenationSpec extends Specification {
  def "Groups are built correctly" () {
    given:
      List<AuthorizationType> authorizations = [new ApiKey("test", "header")]
      Documentation built = new DocumentationBuilder()
              .resourceListing(new ResourceListingBuilder()
                .authorizations(authorizations)
                .apis([Mock(ApiListingReference)])
                .build())
              .apiListingsByResourceGroupName(newHashMap())
              .build()
    expect:
      built.apiListings.size() == 0
      built.resourceListing.authorizations.size() == 1
  }
}
