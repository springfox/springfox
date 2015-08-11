<!DOCTYPE html>
<html>
<head>
    <title>Swagger UI</title>
    <link rel="icon" type="image/png" href="images/favicon-32x32.png" sizes="32x32" />
    <link rel="icon" type="image/png" href="images/favicon-16x16.png" sizes="16x16" />
    <link href='webjars/springfox-swagger-ui/css/typography.css' media='screen' rel='stylesheet' type='text/css'/>
    <link href='webjars/springfox-swagger-ui/css/reset.css' media='screen' rel='stylesheet' type='text/css'/>
    <link href='webjars/springfox-swagger-ui/css/screen.css' media='screen' rel='stylesheet' type='text/css'/>
    <link href='webjars/springfox-swagger-ui/css/reset.css' media='print' rel='stylesheet' type='text/css'/>
    <link href='webjars/springfox-swagger-ui/css/screen.css' media='print' rel='stylesheet' type='text/css'/>
    <script src='webjars/springfox-swagger-ui/lib/jquery-1.8.0.min.js' type='text/javascript'></script>
    <script src='webjars/springfox-swagger-ui/lib/jquery.slideto.min.js' type='text/javascript'></script>
    <script src='webjars/springfox-swagger-ui/lib/jquery.wiggle.min.js' type='text/javascript'></script>
    <script src='webjars/springfox-swagger-ui/lib/jquery.ba-bbq.min.js' type='text/javascript'></script>
    <script src='webjars/springfox-swagger-ui/lib/handlebars-2.0.0.js' type='text/javascript'></script>
    <script src='webjars/springfox-swagger-ui/lib/underscore-min.js' type='text/javascript'></script>
    <script src='webjars/springfox-swagger-ui/lib/backbone-min.js' type='text/javascript'></script>
    <script src='webjars/springfox-swagger-ui/swagger-ui.min.js' type='text/javascript'></script>
    <script src='webjars/springfox-swagger-ui/springfox.js' type='text/javascript'></script>
    <script src='webjars/springfox-swagger-ui/lib/highlight.7.3.pack.js' type='text/javascript'></script>
    <script src='webjars/springfox-swagger-ui/lib/marked.js' type='text/javascript'></script>

    <!-- enabling this will enable oauth2 implicit scope support -->
    <script src='webjars/springfox-swagger-ui/lib/swagger-oauth.js' type='text/javascript'></script>
    <script type="text/javascript">
        $(function() {

          window.swaggerUi = new SwaggerUi({
            dom_id: "swagger-ui-container",
            supportedSubmitMethods: ['get', 'post', 'put', 'delete', 'patch'],
            onComplete: function(swaggerApi, swaggerUi) {

              $('pre code').each(function(i, e) {
                hljs.highlightBlock(e)
              });

            },
            onFailure: function(data) {
              log("Unable to Load SwaggerUI");
            },
            onComplete: function(data) {
              initializeSpringfox();
            },
            docExpansion: "none",
            apisSorter: "alpha"
          });

          function addApiKeyAuthorization() {
            var key = encodeURIComponent($('#input_apiKey')[0].value);
            log("key: " + key);
            if (key && key.trim() != "") {
              var apiKeyAuth = new SwaggerClient.ApiKeyAuthorization("api_key", key, "query");
              window.swaggerUi.api.clientAuthorizations.add("api_key", apiKeyAuth);
              log("added key " + key);
            }
          }

          $('#input_apiKey').change(addApiKeyAuthorization);

          function log() {
            if ('console' in window) {
              console.log.apply(console, arguments);
            }
          }

          function initializeSpringfox() {
            var security = {};
            window.springfox.securityConfig(function(data) {
              security = data;
              if (security.apiKey) {
                $('#input_apiKey').val(security.apiKey);
                addApiKeyAuthorization();
              }
              if (typeof initOAuth == "function") {
                if (security.clientId && security.appName && security.realm) {
                  initOAuth(security);
                }
              }
            });

            window.springfox.uiConfig(function(data) {
              window.swaggerUi.validatorUrl = data.validatorUrl;
            });
          }
        });
    </script>
</head>

<body class="swagger-section">
<div id='header'>
    <div class="swagger-ui-wrap">
        <a id="logo" href="http://swagger.io">swagger</a>
        <form id='api_selector'>
            <div class='input'>
                <select id="select_baseUrl" name="select_baseUrl"/>
            </div>
            <div class='input'><input placeholder="http://example.com/api" id="input_baseUrl" name="baseUrl" type="text"/></div>
            <div class='input'><input placeholder="api_key" id="input_apiKey" name="apiKey" type="text"/></div>
            <div class='input'><a id="explore" href="#">Explore</a></div>
        </form>
    </div>
</div>

<div id="message-bar" class="swagger-ui-wrap">&nbsp;</div>
<div id="swagger-ui-container" class="swagger-ui-wrap"></div>
</body>
</html>
