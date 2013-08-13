package com.mangofactory.swagger;

import org.junit.Before;
import org.junit.Test;

import static com.google.common.collect.Lists.*;
import static org.junit.Assert.*;

public class SwaggerConfigurationTest {
    private SwaggerConfiguration config;

    @Before
    public void setUp() throws Exception {
        config = new SwaggerConfiguration("2.0", "/some-path");
        config.getExcludedResources().addAll(newArrayList("/pets"));
    }

    @Test
    public void whenDocumentationEndpointUriIsEmpty() {
        assertFalse(config.isExcluded(newArrayList("")));
    }

    @Test
    public void whenDocumentationEndpointUriIsNull() {
        assertFalse(config.isExcluded(null));
    }

    @Test
    public void whenDocumentationEndpointUriIsNotADocumentationUri() {
        assertTrue(config.isExcluded(newArrayList("/pets")));
    }

    @Test
    public void whenDocumentationEndpointUriIsADocumentationUri() {
        assertTrue(config.isExcluded(newArrayList("/api-docs/pets")));
    }

    @Test
    public void whenDocumentationEndpointUriIsADocumentationUriThatDoesNotExist() {
        assertFalse(config.isExcluded(newArrayList("/api-docs/does-not-exist")));
    }
}
