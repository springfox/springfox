window.onload = () => {

    const getBaseUrl = () => {
        const urlMatches = /(.*)\/swagger-ui.html.*/.exec(window.location.href);
        return urlMatches[1];
    };

    (async () => {
        const configUIResponse = await fetch("swagger-resources/configuration/ui");
        const configUI = await configUIResponse.json();

        const configSecurityResponse = await fetch("swagger-resources/configuration/security");
        const configSecurity = await configSecurityResponse.json();

        const resourcesResponse = await fetch("swagger-resources");
        const resources = await resourcesResponse.json();
        resources.forEach(resource => {
            resource.url = getBaseUrl() + resource.location
        });

        const ui = SwaggerUIBundle({
            /*--------------------------------------------*\
             * Core
            \*--------------------------------------------*/
            configUrl: null,
            dom_id: '#swagger-ui',
            dom_node: null,
            spec: {},
            url: "",
            urls: resources,
            /*--------------------------------------------*\
             * Plugin system
            \*--------------------------------------------*/
            layout: 'StandaloneLayout',
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
            defaultModelRendering: 'example',
            displayRequestDuration: false,
            docExpansion: 'list',
            filter: false,
            maxDisplayedTags: null,
            operationsSorter: configUI.apisSorter || 'alpha',
            showExtensions: false,
            tagSorter: configUI.apisSorter || 'alpha',
            /*--------------------------------------------*\
             * Network
            \*--------------------------------------------*/
            oauth2RedirectUrl: null,
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
            clientId: configSecurity.clientId,
            clientSecret: configSecurity.clientSecret,
            realm: configSecurity.realm,
            appName: configSecurity.appName,
            scopeSeparator: " ",
            additionalQueryStringParams: {},
            useBasicAuthenticationWithAccessCodeGrant: false,
        });

        window.ui = ui;
    })();

};
