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

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.javadoc.RootDocImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.Set;

/**
 * Created by vijaya on 4/19/17.
 */
public class DocletGenerator  extends AbstractProcessor {

    private static Logger log = LoggerFactory.getLogger(DocletGenerator.class);

    private static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    private static final String DELETE_MAPPING = "org.springframework.web.bind.annotation.DeleteMapping";
    private static final String GET_MAPPING = "org.springframework.web.bind.annotation.GetMapping";
    private static final String PATCH_MAPPING = "org.springframework.web.bind.annotation.PatchMapping";
    private static final String POST_MAPPING = "org.springframework.web.bind.annotation.PostMapping";
    private static final String PUT_MAPPING = "org.springframework.web.bind.annotation.PutMapping";
    private static final String RETURN = "@return";
    private static final String THROWS = "@throws";
    private static final String VALUE = "value";
    private static final String NEWLINE = "\n";
    private static final String EMPTY = "";
    private static final String METHOD = "method";

    private static final String[] MAPPINGS = new String[] {
            DELETE_MAPPING, GET_MAPPING, PATCH_MAPPING, POST_MAPPING, PUT_MAPPING, REQUEST_MAPPING
    };

    public static boolean start(RootDoc root) {

        RootDocImpl rootDoc = (RootDocImpl) root;
        try {
            FileOutputStream javadoc = new FileOutputStream("META-INF/springfox.javadoc.properties");
            Properties properties = new Properties();

            for (ClassDoc classDoc : rootDoc.classes()) {
                StringBuilder sb = new StringBuilder();
                String defaultMethod = processClass(classDoc, sb);
                String pathRoot = sb.toString();
                for (MethodDoc methodDoc : classDoc.methods()) {
                    processMethod(properties, methodDoc, defaultMethod, pathRoot);

                }
            }
            properties.store(javadoc, "Springfox javadoc properties");
        } catch (Exception ex) {
            log.error("Unexpected error processing Javadoc", ex);
        }
        return true;
    }

    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        return true;
    }

    private static String processClass(ClassDoc classDoc, StringBuilder pathRoot) {
        String defaultMethod = null;
        for (AnnotationDesc annotationDesc : classDoc.annotations()) {
            if (REQUEST_MAPPING.equals(annotationDesc.annotationType().qualifiedTypeName())) {
                for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
                    if (VALUE.equals(pair.element().name())) {
                        String value = pair.value().toString();
                        pathRoot.append(value);
                        if (!value.endsWith("/")) {
                            pathRoot.append("/");
                        }
                    }
                    if (METHOD.equals(pair.element().name())) {
                        defaultMethod = pair.value().toString();
                    }
                }
                break;
            }
        }
        return defaultMethod;
    }

    private static void processMethod(Properties properties, MethodDoc methodDoc, String defaultMethod,
                                      String pathRoot) {
        for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
            String name = annotationDesc.annotationType().name();
            if (isMapping(name)) {
                StringBuilder path = new StringBuilder(pathRoot);
                for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
                    if (VALUE.equals(pair.element().name())) {
                        String value = pair.value().toString();
                        if (path.length() > 0 && value.startsWith("/")) {
                            path.append(value.substring(1)).append(".");
                        } else {
                            path.append(value).append(".");
                        }
                        path.append(pair.value().toString());
                        break;
                    }
                }
                String method = getMethod(annotationDesc, name, defaultMethod);
                if (method != null) {
                    path.append(method);
                    properties.setProperty(path.toString() + ".notes", methodDoc.commentText().
                            replaceAll("\n", ""));
                    for (ParamTag paramTag : methodDoc.paramTags()) {
                        properties.setProperty(path.toString() + ".param." + paramTag.parameterName(),
                                paramTag.parameterComment().replaceAll(NEWLINE, EMPTY));
                    }
                    for (Tag tag : methodDoc.tags()) {
                        if (tag.name().equals(RETURN)) {
                            properties.setProperty(path.toString() + ".return", tag.text().
                                    replaceAll(NEWLINE, EMPTY));
                            break;
                        }
                    }
                    for (Tag tag : methodDoc.tags()) {
                        if (tag.name().equals(THROWS)) {
                            String[] tokens = StringUtils.split(tag.text().trim(), " ");
                            String key = path.toString() + ".throws." + tokens[0];
                            String description = tokens[1].trim().replaceAll(NEWLINE, EMPTY);
                            properties.setProperty(key, description);
                        }
                    }
                }
            }

        }
    }

    private static boolean isMapping(String name) {
        for (String mapping : MAPPINGS) {
            if (mapping.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static String getMethod(AnnotationDesc annotationDesc, String name, String defaultMethod) {
        if (REQUEST_MAPPING.equals(name)) {
            for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
                if (METHOD.equals(pair.element().name())) {
                    return pair.value().toString();
                }
            }
        } else if (PUT_MAPPING.equals(name)) {
            return "PUT";
        } else if (POST_MAPPING.equals(name)) {
            return "POST";
        } else if (PATCH_MAPPING.equals(name)) {
            return "PATCH";
        } else if (GET_MAPPING.equals(name)) {
            return "GET";
        } else if (DELETE_MAPPING.equals(name)) {
            return "DELETE";
        }
        return defaultMethod;
    }

}

