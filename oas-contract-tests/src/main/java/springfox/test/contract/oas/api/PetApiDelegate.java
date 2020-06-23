/*
 *
 *  * Copyright 2019-2020 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package springfox.test.contract.oas.api;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;
import springfox.test.contract.oas.model.ModelApiResponse;
import springfox.test.contract.oas.model.Pet;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * A delegate to be called by the {@link PetApiController}}.
 * Implement this interface with a {@link org.springframework.stereotype.Service} annotated class.
 */
@javax.annotation.Generated(value = "org.springdoc.demo.app2.codegen.languages.SpringCodegen",
                            date = "2019-07-11T00:09:29.839+02:00[Europe/Paris]")

public interface PetApiDelegate {

  default Optional<NativeWebRequest> getRequest() {
    return Optional.empty();
  }

  default void addPet(Pet pet) {

  }

  default ResponseEntity<Void> deletePet(
      Long petId,
      String apiKey) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

  }

  default ResponseEntity<java.util.List<Pet>> findPetsByStatus(java.util.List<String> status) {
    extract();
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

  }

  default void extract() {
    getRequest().ifPresent(request -> {
      for (org.springframework.http.MediaType mediaType : org.springframework.http.MediaType
          .parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(org.springframework.http.MediaType.valueOf("application/json"))) {
          ApiUtil.setExampleResponse(
              request,
              "application/json",
              "{  \"photoUrls\" : [ \"photoUrls\", \"photoUrls\" ],  \"name\" : \"doggie\",  \"id\" : 0,  "
                  + "\"category\" : {    \"name\" : \"name\",    \"id\" : 6  },  \"tags\" : [ {    \"name\" : "
                  + "\"name\",    \"id\" : 1  }, {    \"name\" : \"name\",    \"id\" : 1  } ],  \"status\" : "
                  + "\"available\"}");
          break;
        }
        if (mediaType.isCompatibleWith(org.springframework.http.MediaType.valueOf("application/xml"))) {
          ApiUtil.setExampleResponse(
              request,
              "application/xml",
              "<Pet>  <id>123456789</id>  <name>doggie</name>  <photoUrls>    <photoUrls>aeiou</photoUrls>  "
                  + "</photoUrls>  <tags>  </tags>  <status>aeiou</status></Pet>");
          break;
        }
      }
    });
  }

  default ResponseEntity<java.util.List<Pet>> findPetsByTags(java.util.List<String> tags) {
    extract();
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

  }

  default ResponseEntity<Pet> getPetById(Long petId) {
    extract();
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

  }

  default ResponseEntity<Void> updatePet(Pet pet) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

  }

  default ResponseEntity<Void> updatePetWithForm(
      Long petId,
      String name,
      String status) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

  }

  default ResponseEntity<ModelApiResponse> uploadFile(
      Long petId,
      String additionalMetadata,
      @javax.validation.Valid MultipartFile file) {
    getRequest().ifPresent(request -> {
      for (org.springframework.http.MediaType mediaType : org.springframework.http.MediaType
          .parseMediaTypes(request.getHeader("Accept"))) {
        if (mediaType.isCompatibleWith(org.springframework.http.MediaType.valueOf("application/json"))) {
          ApiUtil.setExampleResponse(
              request,
              "application/json",
              "{  \"code\" : 0,  \"type\" : \"type\",  \"message\" : \"message\"}");
          break;
        }
      }
    });
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

  }

  default ResponseEntity<java.util.List<Pet>> getAllPets(@NotNull Pageable pageable) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

}
