package springfox.documentation.uploader;

import springfox.documentation.service.Documentation;

import java.util.Map;

public interface SwaggerHubFileUploader {

    void uploadSwaggerDescriptors(Map<String, Documentation> descriptors);
}
