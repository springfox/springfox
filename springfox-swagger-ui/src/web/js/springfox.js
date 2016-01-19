$(function() {
  var springfox = {
    "baseUrl": function() {
      var urlMatches = /(.*)\/swagger-ui.html.*/.exec(window.location.href);
      return urlMatches[1];
    },
    "securityConfig": function(cb) {
      $.getJSON(this.baseUrl() + "/configuration/security", function(data) {
        cb(data);
      });
    },
    "uiConfig": function(cb) {
      $.getJSON(this.baseUrl() + "/configuration/ui", function(data) {
        cb(data);
      });
    }
  };
  window.springfox = springfox;
  window.oAuthRedirectUrl = springfox.baseUrl() + '/webjars/springfox-swagger-ui/o2c.html';

  window.springfox.uiConfig(function(data) {
    window.swaggerUi = new SwaggerUi({
      dom_id: "swagger-ui-container",
      validatorUrl: data.validatorUrl,
      supportedSubmitMethods: ['get', 'post', 'put', 'delete', 'patch'],
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
      docExpansion: data.docExpansion || 'none',
      jsonEditor: data.jsonEditor || false,
      apisSorter: data.apisSorter || 'alpha',
      defaultModelRendering: data.defaultModelRendering || 'schema',
      showRequestHeaders: data.showRequestHeaders || true
    });

    initializeBaseUrl();

    function addApiKeyAuthorization() {
      var key = encodeURIComponent($('#input_apiKey')[0].value);
      if (key && key.trim() != "") {
        var apiKeyAuth = new SwaggerClient.ApiKeyAuthorization(window.apiKeyName, key, window.apiKeyVehicle);
        window.swaggerUi.api.clientAuthorizations.add(window.apiKeyName, apiKeyAuth);
        log("added key " + key);
      }
    }

    $('#input_apiKey').change(addApiKeyAuthorization);

    function log() {
      if ('console' in window) {
        console.log.apply(console, arguments);
      }
    }

    function oAuthIsDefined(security) {
      return security.clientId
          && security.clientSecret
          && security.appName
          && security.realm;
    }

    function initializeSpringfox() {
      var security = {};
      window.springfox.securityConfig(function(data) {
        security = data;
        window.apiKeyVehicle = security.apiKeyVehicle || 'query';
        window.apiKeyName = security.apiKeyName || 'api_key';
        if (security.apiKey) {
          $('#input_apiKey').val(security.apiKey);
          addApiKeyAuthorization();
        }
        if (typeof initOAuth == "function" && oAuthIsDefined(security)) {
          initOAuth(security);
        }
      });
    }
  });

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
    var relativeLocation = springfox.baseUrl();

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

});


