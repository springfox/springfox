package springfox.service.model

import spock.lang.Specification
import springfox.documentation.builders.DocumentationBuilder
import springfox.documentation.builders.ResourceListingBuilder
import springfox.documentation.service.ApiKey
import springfox.documentation.service.ApiListingReference
import springfox.documentation.service.AuthorizationType
import springfox.documentation.service.Documentation

import static com.google.common.collect.Maps.*

class DocumenationSpec extends Specification {
  def "Groups are built correctly" () {
    given:
      List<AuthorizationType> authorizations = [new ApiKey("api-key", "test", "header",)]
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
