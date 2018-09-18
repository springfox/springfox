/*
 *
 *  Copyright 2018 the original author or authors.
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
package springfox.documentation.uploader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.DocumentationCache;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Class that starts the process of auto-upload of Swagger files to all the providers configured by including 
 * in the classpath their Maven artifact including their implementation of {@link FileUploader}.
 *
 * @author Esteban Cristóbal Rodríguez
 */
@Component
public class FileUploaderLauncher {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploaderLauncher.class);

    private DocumentationCache documentationCache;

    private List<FileUploader> fileUploaders;

    @Autowired
    public FileUploaderLauncher(
            final DocumentationCache documentationCache, final List<FileUploader> fileUploaders) {
        this.documentationCache = documentationCache;
        this.fileUploaders = fileUploaders;
    }

    @PostConstruct
    public void init() {
        for (FileUploader fileUploader : this.fileUploaders) {
            fileUploader.uploadSwaggerDescriptors(this.documentationCache.all());
        }
    }

}
