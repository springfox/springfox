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
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.Doclet;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.javadoc.RootDocImpl;

import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Generate properties file based on Javadoc.
 */
public class DocletGenerator  extends Doclet {

    public static final String SPRINGFOX_JAVADOC_PROPERTIES = "META-INF/springfox.javadoc.properties";

    private static final String REQUEST_MAPPING = "org.springframework.web.bind.annotation.RequestMapping";
    private static final String REQUEST_GET_MAPPING = "org.springframework.web.bind.annotation.RequestMethod.GET";
    private static final String REQUEST_POST_MAPPING = "org.springframework.web.bind.annotation.RequestMethod.POST";
    private static final String REQUEST_PUT_MAPPING = "org.springframework.web.bind.annotation.RequestMethod.PUT";
    private static final String REQUEST_PATCH_MAPPING = "org.springframework.web.bind.annotation.RequestMethod.PATCH";
    private static final String REQUEST_DELETE_MAPPING = "org.springframework.web.bind.annotation.RequestMethod.DELETE";
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

    private static final String[][] REQUEST_MAPPINGS = new String[][] { {REQUEST_DELETE_MAPPING, "DELETE"},
            {REQUEST_GET_MAPPING, "GET"}, {REQUEST_PATCH_MAPPING, "PATCH"}, {REQUEST_POST_MAPPING, "POST"},
            {REQUEST_PUT_MAPPING, "PUT"}};

    private static String getClassDir(String[][] options) {
        for (String[] opt : options) {
            if (opt[0].equalsIgnoreCase("-classdir")) {
                return opt[1];
            }
        }
        return null;
    }

    public static int optionLength(String option) {
        int length = 0;
        if (option.equalsIgnoreCase("-classdir")) {
            length = 2;
        }
        return length;
    }

    public static boolean validOptions(String options[][], DocErrorReporter reporter) {
        boolean foundClassDir = false;
        for (String[] opt : options) {
            if (opt[0].equalsIgnoreCase("-classdir")) {
                if (foundClassDir) {
                    reporter.printError("Only one -classdir option allowed.");
                    return false;
                } else {
                    foundClassDir = true;
                }
            }
        }
        if (!foundClassDir) {
            reporter.printError("Usage: javadoc -classDir classes directory  -doclet  ...");
        }
        return foundClassDir;
    }



    public static boolean start(RootDoc root) {

        RootDocImpl rootDoc = (RootDocImpl) root;
        try {
            String classDir = getClassDir(root.options());
            OutputStream javadoc = null;
            if (classDir == null || classDir.length() == 0) {
                root.printError("No output location was specified");
                return false;
            } else {
                StringBuilder sb = new StringBuilder(classDir);
                if (!classDir.endsWith("/")) {
                    sb.append("/");
                }
                sb.append(SPRINGFOX_JAVADOC_PROPERTIES);
                String out = sb.toString();
                root.printNotice("Writing output to " + out);
                File file = new File(out);
                file.getParentFile().mkdirs();
                javadoc = new FileOutputStream(file);
            }
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
            root.printError("Unexpected error processing Javadoc " + ex.getMessage());
        }
        return true;
    }

    private static String processClass(ClassDoc classDoc, StringBuilder pathRoot) {
        String defaultMethod = null;
        for (AnnotationDesc annotationDesc : classDoc.annotations()) {
            if (REQUEST_MAPPING.equals(annotationDesc.annotationType().qualifiedTypeName())) {
                for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
                    if (VALUE.equals(pair.element().name())) {
                        setRoot(pathRoot, pair);
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

    private static void setRoot(StringBuilder pathRoot, AnnotationDesc.ElementValuePair pair) {
        String value = pair.value().toString();
        if (!value.startsWith("/")) {
            pathRoot.append("/");
        }
        if (value.endsWith("/")) {
            pathRoot.append(value.substring(0, value.length() - 1));
        } else {
            pathRoot.append(value);
        }
    }

    private static void processMethod(Properties properties, MethodDoc methodDoc, String defaultMethod,
                                      String pathRoot) {
        for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
            String name = annotationDesc.annotationType().toString();
            if (isMapping(name)) {
                StringBuilder path = new StringBuilder(pathRoot);
                for (AnnotationDesc.ElementValuePair pair : annotationDesc.elementValues()) {
                    if (VALUE.equals(pair.element().name())) {
                        appendPath(path, pair);
                        break;
                    }
                }
                String method = getMethod(annotationDesc, name, defaultMethod);
                if (method != null) {
                    path.append(method);
                    saveProperty(properties, path.toString() + ".notes", methodDoc.commentText());

                    for (ParamTag paramTag : methodDoc.paramTags()) {
                        saveProperty(properties, path.toString() + ".param." + paramTag.parameterName(),
                                paramTag.parameterComment());
                    }
                    for (Tag tag : methodDoc.tags()) {
                        if (tag.name().equals(RETURN)) {
                            saveProperty(properties, path.toString() + ".return", tag.text());
                            break;
                        }
                    }
                    for (Tag tag : methodDoc.tags()) {
                        if (tag.name().equals(THROWS)) {
                            processThrows(properties, tag, path);
                        }
                    }
                }
            }
        }
    }

    private static void appendPath(StringBuilder path, AnnotationDesc.ElementValuePair pair) {
        String value = pair.value().toString().replaceAll("\"$|^\"", "");
        if (value.startsWith("/")) {
            path.append(value).append(".");
        } else {
            path.append("/").append(value).append(".");
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
                    return resolveRequestMethod(pair, defaultMethod);
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

    private static String resolveRequestMethod(AnnotationDesc.ElementValuePair pair, String defaultMethod) {
        String value = pair.value().toString();
        for (int i = 0; i < REQUEST_MAPPINGS.length; ++i) {
            if (REQUEST_MAPPINGS[i][0].equals(value)) {
                return REQUEST_MAPPINGS[i][1];
            }
        }
        return defaultMethod;
    }

    private static void processThrows(Properties properties, Tag tag, StringBuilder path) {
        String[] tokens = StringUtils.split(tag.text().trim(), " ");
        if (tokens.length == 2) {
            String key = path.toString() + ".throws." + tokens[0];
            saveProperty(properties, key, tokens[1].trim());
        }
    }

    private static void saveProperty(Properties properties, String key, String value) {
        value = value.replaceAll(NEWLINE, EMPTY);
        if (value.length() > 0) {
            properties.setProperty(key, value);
        }
    }
}

