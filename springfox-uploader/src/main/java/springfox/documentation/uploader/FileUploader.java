package springfox.documentation.uploader;

import springfox.documentation.service.Documentation;

import java.util.Map;

public interface FileUploader {

    void uploadSwaggerDescriptors(Map<String, Documentation> descriptors);
}
