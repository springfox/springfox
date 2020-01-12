/*
 *
 *  Copyright 2015 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package springfox.documentation.spring.web.mixins

import springfox.documentation.service.ApiListing
import springfox.documentation.service.OAuth
import springfox.documentation.service.ApiListingReference
import springfox.documentation.service.ResourceListing

trait ApiListingSupport {

   def apiListing(authorizations = [], models = null) {
      new ApiListing(
          "1.0"
          ,
          "",
          "/relative-path-to-endpoint",
          [] as Set,
          [] as Set,
          "",
          [] as Set,
          authorizations,
          [],
          models,
          null,
          1, ["test"] as Set)
   }

   def apiListingReference() {
      new ApiListingReference("/path", "description", 3)
   }

   def resourceListing(List<OAuth> authorizationTypes) {
      new ResourceListing(
              "apiVersion"
              ,
              [apiListingReference()],
              authorizationTypes,
              null)
   }
}
