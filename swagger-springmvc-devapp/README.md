## Swagger Springmvc Devapp

A development aid to allow running of a real spring application which uses swagger-springmvc. 

- Allows for easier manual testing of compiled code rather than having to install to a local maven repo.
- TODO - integrate swagger-ui
  
### Runing

```bash

./gradlew :swagger-springmvc-devapp:bootRun
```

To Run/debugging in an IDE executable: com.mangofactory.swagger.devapp.Application

[http://localhost:8080/api-docs](http://localhost:8080/api-docs)
