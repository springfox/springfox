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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.server.ResponseStatusException;
import springfox.test.contract.oas.model.Order;
import springfox.test.contract.oas.repository.OrderRepository;
import springfox.test.contract.oas.repository.PetRepository;

import javax.annotation.PostConstruct;
import java.util.Date;

@Service
public class StoreApiDelegateImpl implements StoreApiDelegate {

  private final OrderRepository orderRepository;

  @SuppressWarnings("unused")
  private final PetRepository petRepository;

  private final NativeWebRequest request;

  public StoreApiDelegateImpl(
      OrderRepository orderRepository,
      PetRepository petRepository,
      NativeWebRequest request) {
    this.orderRepository = orderRepository;
    this.petRepository = petRepository;
    this.request = request;
  }

  private static Order createOrder(
      long id,
      long petId,
      Order.StatusEnum status) {
    return new Order()
        .id(id)
        .petId(petId)
        .quantity(2)
        .shipDate(new Date())
        .status(status);
  }

  @PostConstruct
  void initOrders() {
    orderRepository.save(createOrder(1, 1, Order.StatusEnum.PLACED));
    orderRepository.save(createOrder(2, 1, Order.StatusEnum.DELIVERED));
    orderRepository.save(createOrder(3, 2, Order.StatusEnum.PLACED));
    orderRepository.save(createOrder(4, 2, Order.StatusEnum.DELIVERED));
    orderRepository.save(createOrder(5, 3, Order.StatusEnum.PLACED));
    orderRepository.save(createOrder(6, 3, Order.StatusEnum.PLACED));
    orderRepository.save(createOrder(7, 3, Order.StatusEnum.PLACED));
    orderRepository.save(createOrder(8, 3, Order.StatusEnum.PLACED));
    orderRepository.save(createOrder(9, 3, Order.StatusEnum.PLACED));
    orderRepository.save(createOrder(10, 3, Order.StatusEnum.PLACED));
  }

  @Override
  public ResponseEntity<Void> deleteOrder(Long orderId) {
    Order order = orderRepository.findById(orderId)
                                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    orderRepository.delete(order);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<java.util.Map<String, Integer>> getInventory() {
    ApiUtil.checkApiKey(request);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Order> getOrderById(Long orderId) {
    return orderRepository.findById(orderId)
                          .map(ResponseEntity::ok)
                          .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
  }

  @Override
  public ResponseEntity<Order> placeOrder(Order order) {
    return ResponseEntity.ok(orderRepository.save(order));
  }
}
