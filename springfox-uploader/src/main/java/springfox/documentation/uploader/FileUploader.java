package springfox.documentation.uploader;

import springfox.documentation.service.Documentation;

import java.util.Map;

/**
 * Interface to be implemented by any provider who aims to be supported by the auto-upload feature.
 *
 * @author Esteban Cristóbal Rodríguez
 */
public interface FileUploader {

    /**
     * Uploads the descriptors passed as parameter to the provider implementing this interface.
     *
     * @param descriptors Swagger descriptors (values) to be uploaded, and endpoints (keys) they belong to.
     */
    void uploadSwaggerDescriptors(Map<String, Documentation> descriptors);
}
