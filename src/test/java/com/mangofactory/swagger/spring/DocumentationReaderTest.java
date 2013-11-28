package com.mangofactory.swagger.spring;

import com.mangofactory.swagger.ControllerDocumentation;
import com.mangofactory.swagger.spring.controller.DocumentationController;
import com.mangofactory.swagger.spring.sample.configuration.ServicesTestConfiguration;
import com.wordnik.swagger.core.Documentation;
import com.wordnik.swagger.core.DocumentationEndPoint;
import com.wordnik.swagger.core.DocumentationOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.test.context.WebContextLoader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        loader = WebContextLoader.class,
        classes = ServicesTestConfiguration.class)
public class DocumentationReaderTest {

    @Autowired
    private DocumentationController controller;
    @Mock
    private HttpServletRequest request;
    private Documentation resourceListing;
    private DocumentationEndPoint petsEndpoint;
    private DocumentationEndPoint businessEndpoint;
    private DocumentationEndPoint featuresEndpoint;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn("/pets");
        resourceListing = controller.getResourceListing();
        for (DocumentationEndPoint endPoint : resourceListing.getApis()) {
            if("/api-docs/pets".equals(endPoint.getPath())) {
                petsEndpoint = endPoint;
            } else if("/api-docs/business-controller".equals(endPoint.getPath())) {
                businessEndpoint = endPoint;
            } else if("/api-docs/features".equals(endPoint.getPath())) {
                featuresEndpoint = endPoint;
            }
        }

    }

    @Test
    public void rootDocumentationEndpointPointsToApiDocs() {
        assertThat(petsEndpoint.getPath(), equalTo("/api-docs/pets"));
    }

    @Test
    public void expectExcludedResourcesToBeExcluded() {
        for (DocumentationEndPoint endPoint : resourceListing.getApis()) {
            if("/api-docs/excluded".equals(endPoint.getPath())) {
                fail("Excluded resources should not be documented");
            }
        }
    }

    @Test
    public void findsDeclaredHandlerMethods() {
        assertThat(resourceListing.getApis().size(), equalTo(8));
        assertThat("/api-docs/pets", equalTo(petsEndpoint.getPath()));
        Documentation petsDocumentation = controller.getApiDocumentation(request);
        assertThat(petsDocumentation, notNullValue());
    }

    @Test
    public void findsExpectedMethods() {
        when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn("/features");
        ControllerDocumentation petsDocumentation = controller.getApiDocumentation(request);


        String requestUri = "/features/allMethodsAllowed";
        DocumentationOperation operation = petsDocumentation.getEndPoint(requestUri, RequestMethod.GET).iterator().next();
        assertThat(operation, is(notNullValue()));
        operation = petsDocumentation.getEndPoint(requestUri, RequestMethod.POST).iterator().next();
        assertThat(operation, is(notNullValue()));
        operation = petsDocumentation.getEndPoint(requestUri, RequestMethod.DELETE).iterator().next();
        assertThat(operation, is(notNullValue()));
        operation = petsDocumentation.getEndPoint(requestUri, RequestMethod.PUT).iterator().next();
        assertThat(operation, is(notNullValue()));
    }

    @Test
    public void shouldLocateDocsOnControllerWithoutTopLevelRequestMapping(){
        when(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE)).thenReturn("/business-service");
        resourceListing = controller.getResourceListing();

        ControllerDocumentation documentation = controller.getApiDocumentation(request);
        List<DocumentationOperation> endPoint = documentation.getEndPoint("/businesses/vanilla/{businessId}", RequestMethod.GET);
        DocumentationOperation operation = endPoint.iterator().next();
        assertThat(operation, is(notNullValue()));
        assertThat(operation.getParameters().size(), equalTo(1));
        assertThat(operation.getSummary(), equalTo("get Vanilla Path Variable"));
    }
    //TODO: Move to feature demonstration service
//    @Test
//    public void findsExpectedMethods() {
//        ControllerDocumentation petsDocumentation = controller.getApiDocumentation(request);
//        DocumentationOperation operation = petsDocumentation.getEndPoint("/pets/{petId}",
//                RequestMethod.GET).iterator().next();
//        assertThat(operation, is(notNullValue()));
//        assertThat(operation.getParameters().size(), equalTo(1));
//
//        operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.GET).iterator().next();
//        assertThat(operation, is(notNullValue()));
//        operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.POST).iterator().next();
//        assertThat(operation, is(notNullValue()));
//        operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.DELETE).iterator().next();
//        assertThat(operation, is(notNullValue()));
//        operation = petsDocumentation.getEndPoint("/pets/allMethodsAllowed", RequestMethod.PUT).iterator().next();
//        assertThat(operation, is(notNullValue()));
//    }
}
