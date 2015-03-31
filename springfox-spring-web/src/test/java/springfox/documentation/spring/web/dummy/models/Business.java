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

package springfox.documentation.spring.web.dummy.models;

import com.wordnik.swagger.annotations.ApiModel;
import org.joda.time.LocalDate;

import java.util.Date;


@ApiModel(value = "Biz")
public class Business {
   //not private - just for testing
   public int id;
   public String name;
   public String owner;
   public LocalDate inception;
   public BusinessType businessType = BusinessType.PRODUCT;
   public Date taxDate = new Date();

   public enum BusinessType {
      PRODUCT(1),
      SERVICE(2);
      private int value;

      private BusinessType(int value) {
         this.value = value;
      }

      public int getValue() {
         return value;
      }
   }
}


