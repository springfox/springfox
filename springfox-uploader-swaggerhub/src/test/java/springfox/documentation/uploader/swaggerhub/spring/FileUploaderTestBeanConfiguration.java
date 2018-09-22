package springfox.documentation.uploader.swaggerhub.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.json.JsonSerializer;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2Mapper;

import static org.mockito.Mockito.mock;

@Configuration
public class FileUploaderTestBeanConfiguration {

    @Bean
    public ServiceModelToSwagger2Mapper mapper() {
        final ServiceModelToSwagger2Mapper mapper = mock(ServiceModelToSwagger2Mapper.class);
        return mapper;
    }

    @Bean
    public JsonSerializer serializer() {
        final JsonSerializer serializer = mock(JsonSerializer.class);
        return serializer;
    }

    @Bean
    public DocumentationCache documentationCache() {
        final DocumentationCache documentationCache = mock(DocumentationCache.class);
        return documentationCache;
    }

    @Bean
    public MockRestServiceServer server(final RestTemplate restTemplate) {
        return MockRestServiceServer.bindTo(restTemplate).build();
    }
}
