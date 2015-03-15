package springdox.service.model

import spock.lang.Specification
import springdox.documentation.builders.DocumentationBuilder
import springdox.documentation.builders.ResourceListingBuilder
import springdox.documentation.service.ApiKey
import springdox.documentation.service.ApiListingReference
import springdox.documentation.service.AuthorizationType
import springdox.documentation.service.Documentation

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
