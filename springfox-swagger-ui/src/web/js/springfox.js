window.onload = () => {

  const getBaseURL = () => {
    const urlMatches = /(.*)\/swagger-ui.html.*/.exec(window.location.href);
    return urlMatches[1];
  };

  const buildSystemAsync = async (baseUrl) => {
    try {
      const configUIResponse = await fetch(baseUrl + "/swagger-resources/configuration/ui");
      const configUI = await configUIResponse.json();

      const configSecurityResponse = await fetch(baseUrl + "/swagger-resources/configuration/security");
      const configSecurity = await configSecurityResponse.json();

      const resourcesResponse = await fetch(baseUrl + "/swagger-resources");
      const resources = await resourcesResponse.json();
      resources.forEach(resource => {
        resource.url = baseUrl + resource.location;
      });

      window.ui = getUI(baseUrl, resources, configUI, configSecurity);
    } catch (e) {
      const retryURL = await prompt(
        "Unable to infer base url. This is common when using dynamic servlet registration or when" +
        " the API is behind an API Gateway. The base url is the root of where" +
        " all the swagger resources are served. For e.g. if the api is available at http://example.org/api/v2/api-docs" +
        " then the base url is http://example.org/api/. Please enter the location manually: ",
        window.location.href);

      return buildSystemAsync(retryURL);
    }
  };

  const getUI = (baseUrl, resources, configUI, configSecurity) => {
    const ui = SwaggerUIBundle({
      /*--------------------------------------------*\
       * Core
      \*--------------------------------------------*/
      configUrl: null,
      dom_id: "#swagger-ui",
      dom_node: null,
      spec: {},
      url: "",
      urls: resources,
      /*--------------------------------------------*\
       * Plugin system
      \*--------------------------------------------*/
      layout: "StandaloneLayout",
      plugins: [
        SwaggerUIBundle.plugins.DownloadUrl
      ],
      presets: [
        SwaggerUIBundle.presets.apis,
        SwaggerUIStandalonePreset
      ],
      /*--------------------------------------------*\
       * Display
      \*--------------------------------------------*/
      deepLinking: true,
      displayOperationId: false,
      defaultModelsExpandDepth: 1,
      defaultModelExpandDepth: 1,
      defaultModelRendering: resources.defaultModelRendering,
      displayRequestDuration: false,
      docExpansion: resources.docExpansion,
      filter: false,
      maxDisplayedTags: null,
      operationsSorter: configUI.apisSorter || "alpha",
      showExtensions: false,
      tagSorter: configUI.apisSorter || "alpha",
      /*--------------------------------------------*\
       * Network
      \*--------------------------------------------*/
      oauth2RedirectUrl: baseUrl + "/webjars/springfox-swagger-ui/oauth2-redirect.html",
      requestInterceptor: (a => a),
      responseInterceptor: (a => a),
      showMutatedRequest: true,
      validatorUrl: null,
      /*--------------------------------------------*\
       * Macros
      \*--------------------------------------------*/
      modelPropertyMacro: null,
      parameterMacro: null,
    });

    ui.initOAuth({
      /*--------------------------------------------*\
       * OAuth
      \*--------------------------------------------*/
      clientId: configSecurity.clientId,
      clientSecret: configSecurity.clientSecret,
      realm: configSecurity.realm,
      appName: configSecurity.appName,
      scopeSeparator: configSecurity.scopeSeparator,
      additionalQueryStringParams: {},
      useBasicAuthenticationWithAccessCodeGrant: false,
    });

    return ui;
  };

  /* Entry Point */
  (async () => {
    await buildSystemAsync(getBaseURL());
  })();

};
