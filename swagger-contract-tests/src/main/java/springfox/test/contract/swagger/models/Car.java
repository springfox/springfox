/*
 *
 *  Copyright 2017-2019 the original author or authors.
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
package springfox.test.contract.swagger.models;

public class Car extends Vehicle {
  private int seatingCapacity;
  private double topSpeed;

  public Car() {
  }

  public Car(String make, String model, int seatingCapacity, double topSpeed) {
    super(make, model);
    this.seatingCapacity = seatingCapacity;
    this.topSpeed = topSpeed;
  }

  public int getSeatingCapacity() {
    return seatingCapacity;
  }

  public void setSeatingCapacity(int seatingCapacity) {
    this.seatingCapacity = seatingCapacity;
  }

  public double getTopSpeed() {
    return topSpeed;
  }

  public void setTopSpeed(double topSpeed) {
    this.topSpeed = topSpeed;
  }
}