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
import org.springframework.web.server.ResponseStatusException;
import springfox.test.contract.oas.model.User;
import springfox.test.contract.oas.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class UserApiDelegateImpl implements UserApiDelegate {

  private final UserRepository userRepository;

  public UserApiDelegateImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  private static User createUser(
      long id,
      String username,
      String firstName,
      String lastName,
      String email,
      int userStatus) {
    return new User()
        .id(id)
        .username(username)
        .firstName(firstName)
        .lastName(lastName)
        .email(email)
        .password("XXXXXXXXXXX")
        .phone("123-456-7890")
        .userStatus(userStatus);
  }

  @PostConstruct
  private void initUsers() {
    userRepository.save(createUser(1, "user1", "first name 1", "last name 1",
                                   "email1@test.com", 1));
    userRepository.save(createUser(2, "user2", "first name 2", "last name 2",
                                   "email2@test.com", 2));
    userRepository.save(createUser(3, "user3", "first name 3", "last name 3",
                                   "email3@test.com", 3));
    userRepository.save(createUser(4, "user4", "first name 4", "last name 4",
                                   "email4@test.com", 1));
    userRepository.save(createUser(5, "user5", "first name 5", "last name 5",
                                   "email5@test.com", 2));
    userRepository.save(createUser(6, "user6", "first name 6", "last name 6",
                                   "email6@test.com", 3));
    userRepository.save(createUser(7, "user7", "first name 7", "last name 7",
                                   "email7@test.com", 1));
    userRepository.save(createUser(8, "user8", "first name 8", "last name 8",
                                   "email8@test.com", 2));
    userRepository.save(createUser(9, "user9", "first name 9", "last name 9",
                                   "email9@test.com", 3));
    userRepository.save(createUser(10, "user10", "first name 10", "last name 10",
                                   "email10@test.com", 1));
    userRepository.save(createUser(11, "user?10", "first name ?10", "last name ?10",
                                   "email101@test.com", 1));
  }

  @Override
  public ResponseEntity<Void> createUser(User user) {
    userRepository.save(user);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> createUsersWithArrayInput(java.util.List<User> users) {
    userRepository.saveAll(users);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> createUsersWithListInput(java.util.List<User> users) {
    return createUsersWithArrayInput(users);
  }

  @Override
  public ResponseEntity<Void> deleteUser(String username) {
    User user = userRepository.findById(username)
                              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    userRepository.delete(user);
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<User> getUserByName(String username) {
    return userRepository.findById(username)
                         .map(ResponseEntity::ok)
                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @Override
  public ResponseEntity<String> loginUser(
      String username,
      String password) {
    Instant now = Instant.now().plus(1, ChronoUnit.HOURS);
    return ResponseEntity.ok()
                         .header("X-Expires-After", new Date(now.toEpochMilli()).toString())
                         .header("X-Rate-Limit", "5000")
                         .body("logged in user session:" + now.toEpochMilli());
  }

  @Override
  public ResponseEntity<Void> logoutUser() {
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Void> updateUser(
      String username,
      User user) {
    user.setUsername(username);
    return createUser(user);
  }
}
