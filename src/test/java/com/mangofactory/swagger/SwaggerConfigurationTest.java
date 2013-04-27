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
        assertFalse(config.isExcluded(""));
    }

    @Test
    public void whenDocumentationEndpointUriIsNull() {
        assertFalse(config.isExcluded(null));
    }

    @Test
    public void whenDocumentationEndpointUriIsNotADocumentationUri() {
        assertTrue(config.isExcluded("/pets"));
    }

    @Test
    public void whenDocumentationEndpointUriIsADocumentationUri() {
        assertTrue(config.isExcluded("/api-docs/pets"));
    }

    @Test
    public void whenDocumentationEndpointUriIsADocumentationUriThatDoesNotExist() {
        assertFalse(config.isExcluded("/api-docs/does-not-exist"));
    }
}
