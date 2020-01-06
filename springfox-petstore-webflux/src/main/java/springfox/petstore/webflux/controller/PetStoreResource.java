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

package springfox.petstore.webflux.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;
import springfox.petstore.webflux.model.Order;
import springfox.petstore.webflux.model.Pet;
import springfox.petstore.webflux.repository.MapBackedRepository;

import static org.springframework.http.MediaType.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;
import static springfox.petstore.webflux.Responses.*;

@Controller
@RequestMapping(value = "/api/store", produces = APPLICATION_JSON_VALUE)
@Api(value = "/store", description = "Operations about store")
public class PetStoreResource {
  private static StoreData storeData = new StoreData();

  private static class StoreData extends MapBackedRepository<Long, Order> {
  }

  @RequestMapping(value = "/order/{orderId}", method = GET)
  @ApiOperation(
      value = "Find purchase order by ID",
      notes = "For valid response try integer IDs with value <= 5 or > 10. Other values will generated exceptions",
      response = Order.class,
      tags = { "Pet Store" })
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Invalid ID supplied"),
      @ApiResponse(code = 404, message = "Order not found") })
  public Mono<ResponseEntity<Order>> getOrderById(
      @ApiParam(value = "ID of pet that needs to be fetched", allowableValues = "range[1,5]", required = true)
      @PathVariable("orderId") String orderId)
      throws NotFoundException {
    Order order = storeData.get(Long.valueOf(orderId));
    if (null != order) {
      return Mono.just(ok(order));
    } else {
      throw new NotFoundException(404, "Order not found");
    }
  }

  @RequestMapping(value = "/order", method = POST)
  @ApiOperation(value = "Place an order for a pet", response = Order.class)
  @ApiResponses({ @ApiResponse(code = 400, message = "Invalid Order") })
  public Mono<ResponseEntity<String>> placeOrder(
      @ApiParam(value = "order placed for purchasing the pet", required = true) Order order) {
    storeData.add(order);
    return Mono.just(ok(""));
  }

  @RequestMapping(value = "/order/{orderId}", method = DELETE)
  @ApiOperation(
      value = "Delete purchase order by ID", notes = "For valid response try integer IDs with value < 1000. " +
      "Anything above 1000 or non-integers will generate API errors"
  )
  @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"),
      @ApiResponse(code = 404, message = "Order not found") })
  public Mono<ResponseEntity<String>> deleteOrder(
      @ApiParam(value = "ID of the order that needs to be deleted", allowableValues = "range[1,infinity]", required
          = true) @PathVariable("orderId") String orderId) {
    storeData.delete(Long.valueOf(orderId));
    return Mono.just(ok(""));
  }

  @RequestMapping(value = "search", method = GET, produces = "application/json", params = "x=TX")
  @ResponseStatus(value = HttpStatus.OK)
  public Mono<ResponseEntity<Pet>> getPetInTx() {
    throw new UnsupportedOperationException();
  }

  @RequestMapping(value = "search", method = GET, produces = "application/json", params = "x=CA")
  @ResponseStatus(value = HttpStatus.OK)
  public Mono<ResponseEntity<Pet>> getPetInCA() {
    throw new UnsupportedOperationException();
  }
}
