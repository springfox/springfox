$(function() {
  var baseUrl;
  var springfox = {
    "getBaseUrl": function() {
      if (baseUrl === undefined) {
        var urlMatches = /(.*)\/swagger-ui.html.*/.exec(window.location.href);
        baseUrl = urlMatches[1];
      }
      return baseUrl;
    },
    "setBaseUrl": function(override) {
      baseUrl = override;
    },
    "securityConfig": function(onSuccess, onError) {
      $.ajax({
        url: this.getBaseUrl() + "/swagger-resources/configuration/security",
        dataType: 'json',
        success: onSuccess,
        error: onError
      });
    },
    "uiConfig": function(onSuccess, onError) {
      $.ajax({
        url: this.getBaseUrl() + "/swagger-resources/configuration/ui",
        dataType: 'json',
        success: onSuccess,
        error: onError
      });
    }
  };
  window.springfox = springfox;
  window.oAuthRedirectUrl = springfox.getBaseUrl() + '/webjars/springfox-swagger-ui/o2c.html';

  window.springfox.uiConfig(onSuccess, onError);

  $('#select_baseUrl').change(function() {
    window.swaggerUi.headerView.trigger('update-swagger-ui', {
      url: $('#select_baseUrl').val()
    });
  });

  function maybePrefix(location, withRelativePath) {
    var pat = /^https?:\/\//i;
    if (pat.test(location)) {
      return location;
    }
    return withRelativePath + location;
  }

  function initializeBaseUrl() {
    var relativeLocation = springfox.getBaseUrl();

    $('#input_baseUrl').hide();

    $.getJSON(relativeLocation + "/swagger-resources", function(data) {

      var $urlDropdown = $('#select_baseUrl');
      $urlDropdown.empty();
      $.each(data, function(i, resource) {
        var option = $('<option></option>')
            .attr("value", maybePrefix(resource.location, relativeLocation))
            .text(resource.name + " (" + resource.location + ")");
        $urlDropdown.append(option);
      });
      $urlDropdown.change();
    });
  }

  function onSuccess(data) {
    window.swaggerUi = new SwaggerUi({
      dom_id: "swagger-ui-container",
      validatorUrl: data.validatorUrl,
      supportedSubmitMethods: data.supportedSubmitMethods || ['get', 'post', 'put', 'delete', 'patch'],
      docExpansion: data.docExpansion || 'none',
      jsonEditor: JSON.parse(data.jsonEditor) || false,
      apisSorter: data.apisSorter || 'alpha',
      defaultModelRendering: data.defaultModelRendering || 'schema',
      showRequestHeaders: data.showRequestHeaders || true,
      timeout: data.requestTimeout,
      onComplete: function(swaggerApi, swaggerUi) {

        initializeSpringfox();

        if (window.SwaggerTranslator) {
          window.SwaggerTranslator.translate();
        }

        $('pre code').each(function(i, e) {
          hljs.highlightBlock(e)
        });

      },
      onFailure: function(data) {
        log("Unable to Load SwaggerUI");
      },
    });

    initializeBaseUrl();

    function addApiKeyAuthorization(security) {
      var apiKeyVehicle = security.apiKeyVehicle || 'query';
      var apiKeyName = security.apiKeyName || 'api_key';
      var apiKey = security.apiKey || '';
      if (apiKey && apiKey.trim() != "") {
        var apiKeyAuth = new SwaggerClient.ApiKeyAuthorization(apiKeyName, apiKey, apiKeyVehicle);
        window.swaggerUi.api.clientAuthorizations.add(apiKeyName, apiKeyAuth);
        log("added key " + apiKey);
      }
    }

    function log() {
      if ('console' in window) {
        console.log.apply(console, arguments);
      }
    }

    function oAuthIsDefined(security) {
      return security.clientId
          && security.appName
          && security.realm;
    }

    function initializeSpringfox() {
      var security = {};
      window.springfox.securityConfig(function(data) {
        security = data;
        addApiKeyAuthorization(security);
        if (typeof initOAuth == "function" && oAuthIsDefined(security)) {
          initOAuth(security);
        }
      });
    }
  }

  function onError() {
    baseUrl = prompt(
        "Unable to infer base url. This is common when using dynamic servlet registration or when" +
        " the API is behind an API Gateway. The base url is the root of where" +
        " all the swagger resources are served. For e.g. if the api is available at http://example.org/api/v2/api-docs" +
        " then the base url is http://example.org/api/. Please enter the location manually: ",
        window.location.href);
    window.springfox.uiConfig(onSuccess, onError);
  }

});

