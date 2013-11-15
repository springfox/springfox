package com.mangofactory.swagger.configuration;

import org.junit.Test;

public class SwaggerApiTest {
   @Test
   public void testGetApiInfo() throws Exception {
      new SwaggerApi();
      new SpringSwaggerConfig();
      assert true;
   }
}
