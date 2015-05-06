package springfox.documentation.swagger1.web

import org.springframework.http.MediaType
import spock.lang.Specification
import springfox.documentation.swagger1.dto.ApiListing
import springfox.documentation.swagger1.dto.ModelDto
import springfox.documentation.swagger1.dto.ModelPropertyDto

import static com.google.common.collect.Lists.*

class ApiListingMergerSpec extends Specification {

  def "it returns api listing absent when collection is empty"(){
    when:
      def merged = ApiListingMerger.mergedApiListing(apiListings)
    then:
      !merged.isPresent()
    where:
      apiListings << [newArrayList(), null]
  }

  def "it returns api listing absent when collection has one element"(){
    when:
      def merged = ApiListingMerger.mergedApiListing(newArrayList(Mock(ApiListing)))
    then:
      merged.isPresent()
  }

  def "it returns api listing absent when collection has more than one element"(){
    when:
      def merged = ApiListingMerger.mergedApiListing([newApiListing(), newApiListing()])
    then:
      merged.isPresent()
  }

  def newApiListing() {
    ApiListing apiListing = new ApiListing()
    apiListing.apiVersion = '1'
    apiListing.swaggerVersion = '1.2'
    apiListing.basePath = '/base'
    apiListing.resourcePath= '/resource'
    apiListing.consumes= [MediaType.APPLICATION_JSON_VALUE]
    apiListing.produces= [MediaType.APPLICATION_JSON_VALUE]
    apiListing.protocols= []
    apiListing.authorizations= []
    apiListing.apis = []
    apiListing.models =
        ['someModel':
             new ModelDto('id', 'name', 'qtype',
                 ['aprop': new ModelPropertyDto("aProp", 'ptype', 'qtype', 0, false, 'pdesc', null)]
                 , 'desc', null, null, null)
        ]
    apiListing.description = 'description'
    apiListing.position = 0
    apiListing
  }
}
