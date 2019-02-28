/*
 *
 *  Copyright 2017 the original author or authors.
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

package springfox.petstore.webflux.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import io.swagger.annotations.ResponseHeader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.petstore.webflux.Responses;
import springfox.petstore.webflux.model.Pet;
import springfox.petstore.webflux.repository.MapBackedRepository;

import java.util.List;
import java.util.Objects;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@RequestMapping(value = "/api/pet", produces = { APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE })
@Api(value = "/pet", description = "Operations about pets")
public class PetController {

  private static final String WRITE_PETS = "write:pets";
  private static final String READ_PETS = "read:pets";
  private static final String PETSTORE_AUTH = "petstore_auth";
  private PetRepository petData = new PetRepository();

  @RequestMapping(value = "/{petId}", method = GET)
  @ApiOperation(
      value = "Find pet by ID", notes = "Returns a pet when ID < 10. ID > 10 or non-integers will simulate API " +
      "error conditions",
      response = Pet.class,
      responseHeaders = {
          @ResponseHeader(name = "header4", response = String.class),
          @ResponseHeader(name = "header3", response = String.class)
      },
      authorizations = {
          @Authorization(value = "api_key"),
          @Authorization(value = PETSTORE_AUTH, scopes = {
              @AuthorizationScope(scope = WRITE_PETS, description = ""),
              @AuthorizationScope(scope = READ_PETS, description = "")
          }) })
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Invalid ID supplied", responseHeaders = {
          @ResponseHeader(name = "header2", response = String.class),
          @ResponseHeader(name = "header1", response = String.class)
      }),
      @ApiResponse(code = 404, message = "Pet not found") }
  )
  public Mono<ResponseEntity<Pet>> getPetById(
      @ApiParam(value = "ID of pet that needs to be fetched", allowableValues = "range[1,5]", required = true)
      @PathVariable("petId") String petId)
      throws NotFoundException {
    Pet pet = petData.get(Long.valueOf(petId));
    if (null != pet) {
      return Mono.just(Responses.ok(pet));
    } else {
      throw new NotFoundException(404, "Pet not found");
    }
  }

  @RequestMapping(method = POST)
  @ApiOperation(value = "Add a new pet to the store",
      authorizations = @Authorization(value = PETSTORE_AUTH, scopes = {
          @AuthorizationScope(scope = WRITE_PETS, description = ""),
          @AuthorizationScope(scope = READ_PETS, description = "")
      }))
  @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") })
  public Mono<ResponseEntity<String>> addPet(
      @ApiParam(value = "Pet object that needs to be added to the store", required = true) @RequestBody Pet pet) {
    petData.add(pet);
    return Mono.just(Responses.ok("SUCCESS"));
  }

  @RequestMapping(method = PUT)
  @ApiOperation(value = "Update an existing pet",
      authorizations = @Authorization(value = PETSTORE_AUTH, scopes = {
          @AuthorizationScope(scope = WRITE_PETS, description = ""),
          @AuthorizationScope(scope = READ_PETS, description = "")
      }))
  @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"),
      @ApiResponse(code = 404, message = "Pet not found"),
      @ApiResponse(code = 405, message = "Validation exception") })
  public Mono<ResponseEntity<String>> updatePet(
      @ApiParam(value = "Pet object that needs to be added to the store", required = true) @RequestBody Pet pet) {
    petData.add(pet);
    return Mono.just(Responses.ok("SUCCESS"));
  }

  @RequestMapping(value = "/findByStatus", method = GET)
  @ApiOperation(
      value = "Finds Pets by status",
      notes = "Multiple status values can be provided with comma-separated strings",
      response = Pet.class,
      responseContainer = "List",
      authorizations = @Authorization(value = PETSTORE_AUTH, scopes = {
          @AuthorizationScope(scope = WRITE_PETS, description = ""),
          @AuthorizationScope(scope = READ_PETS, description = "")
      }))
  @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid status value") })
  /** TODO: This renders parameter as
   *
   "name": "status",
   "in": "query",
   "description": "Status values that need to be considered for filter",
   "required": false,
   "type": "array",
   "items": {"type": "string"},
   "collectionFormat": "multi",
   "default": "available"
   */
  public Mono<ResponseEntity<List<Pet>>> findPetsByStatus(
      @ApiParam(value = "Status values that need to be considered for filter",
          required = true,
          defaultValue = "available",
          allowableValues = "available,pending,sold",
          allowMultiple = true)
      @RequestParam("status") String status) {
    return Mono.just(Responses.ok(petData.findPetByStatus(status)));
  }

  @RequestMapping(value = "/findByTags", method = GET)
  @ApiOperation(
      value = "Finds Pets by tags",
      notes = "Multiple tags can be provided with comma-separated strings. Use tag1, tag2, tag3 for testing.",
      response = Pet.class,
      responseContainer = "List",
      authorizations = @Authorization(value = PETSTORE_AUTH, scopes = {
          @AuthorizationScope(scope = WRITE_PETS, description = ""),
          @AuthorizationScope(scope = READ_PETS, description = "")
      }))
  @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid tag value") })
  @Deprecated
  /** TODO: This renders the parameter as 
   "name": "tags",
   "in": "query",
   "description": "Tags to filter by",
   "required": false,
   "type": "array",
   "items": {"type": "string"},
   "collectionFormat": "multi" */
  public Flux<Pet> findPetsByTags(
      @ApiParam(
          value = "Tags to filter by",
          required = true,
          allowMultiple = true)
      @RequestParam("tags") String tags) {
    return Flux.fromIterable(petData.findPetByTags(tags));
  }

  @RequestMapping(value = "/findPetsHidden", method = GET)
  @ApiOperation(
      value = "Finds Pets (hidden)",
      notes = "Hidden method",
      response = Pet.class,
      responseContainer = "List",
      hidden = true,
      authorizations = @Authorization(value = PETSTORE_AUTH, scopes = {
          @AuthorizationScope(scope = WRITE_PETS, description = ""),
          @AuthorizationScope(scope = READ_PETS, description = "")
      }))
  @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid tag value") })
  public Mono<ResponseEntity<List<Pet>>> findPetsHidden(
      @ApiParam(
          value = "Tags to filter by",
          required = true,
          allowMultiple = true)
      @RequestParam("tags") String tags) {
    return Mono.just(Responses.ok(petData.findPetByTags(tags)));
  }

  static class PetRepository extends MapBackedRepository<Long, Pet> {
    List<Pet> findPetByStatus(String status) {
      return where(input -> Objects.equals(input.getStatus(), status));
    }

    List<Pet> findPetByTags(String tags) {
      return where(input -> input.getTags().stream()
          .anyMatch(input1 -> Objects.equals(input1.getName(), tags)));
    }
  }
}
