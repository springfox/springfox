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
package springfox.documentation.spring.web.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class JavaDocPropertiesReader {

    private static final Logger logger = LoggerFactory.getLogger(JavaDocPropertiesReader.class);

    private String propertyFilePath = "META-INF/springfox.javadoc.properties";
    private Properties props = new Properties();

    @PostConstruct
    public void init() throws IOException {
        Enumeration<URL> urls = this.getClass().getClassLoader().getResources(propertyFilePath);
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            try {
                InputStream stream = url.openStream();
                try {
                    if (stream != null) {
                        props.load(stream);
                        stream.close();
                    }
                } catch (Exception ex) {
                    logger.error("Unable to use properties from {}", url.getFile(), ex);
                }
            } catch (Exception ex) {
                logger.error("Unable to read from {}", url.getFile(), ex);
            }
        }
    }
    public String getProperty(String key) {
        return props.getProperty(key);
    }

    public String getPropertyFilePath() {
        return propertyFilePath;
    }

    public void setPropertyFilePath(String propertyFilePath) {
        this.propertyFilePath = propertyFilePath;
    }


}
