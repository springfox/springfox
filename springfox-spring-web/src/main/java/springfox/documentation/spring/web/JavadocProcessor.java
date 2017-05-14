/*
 *
 *  Copyright 2015-2016 the original author or authors.
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
package springfox.documentation.spring.web;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.sun.tools.javadoc.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generate properties file based on Javadoc.
 */
public class JavadocProcessor extends AbstractProcessor {

    private static Logger log = LoggerFactory.getLogger(JavadocProcessor.class);

    private String uri = null;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        Filer filer = processingEnv.getFiler();
        FileObject fileObject = null;
        try {
            fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT, "",
                    DocletGenerator.SPRINGFOX_JAVADOC_PROPERTIES, null);

        } catch (IOException ioe) {
            log.error("Unable to access output location for Springfox javadoc properties: {}", ioe);
        }
        uri = fileObject.toUri().toString();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>(Arrays.asList(new String[] {
           "springfox.documentation.annotations.*",
                   "org.springframework.web.bind.annotation.*",
                   "io.swagger.annotations.*"
        }));

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {

        log.info("Processing Javadoc for Swagger");
        if (uri == null) {
            log.error("Skipping swagger annotation pocessing due to missing output location");
            return false;
        }

        CharArrayWriter errorCharArrayWriter = new CharArrayWriter();
        PrintWriter errorWriter = new PrintWriter(errorCharArrayWriter);
        CharArrayWriter warnCharArrayWriter = new CharArrayWriter();
        PrintWriter warnWriter = new PrintWriter(errorCharArrayWriter);
        CharArrayWriter noticeCharArrayWriter = new CharArrayWriter();
        PrintWriter noticeWriter = new PrintWriter(noticeCharArrayWriter);

        int result = Main.execute("Springfox Doclet Generator", errorWriter, warnWriter, noticeWriter,
                DocletGenerator.class.getName(), this.getClass().getClassLoader(),
                DocletGenerator.SPRINGFOX_JAVADOC_URI, uri);
        log.info("Springfox doclet generator completed with status {}", result);
        if (result != 0) {
            String errors = errorCharArrayWriter.toString();
            String warnings = warnCharArrayWriter.toString();
            String notices = noticeCharArrayWriter.toString();
            if (errors.length() > 0) {
                log.error(errors);
            }
            if (warnings.length() > 0) {
                log.warn(warnings);
            }
            if (notices.length() > 0) {
                log.info(notices);
            }
        }
        return result == 0;
    }
}

