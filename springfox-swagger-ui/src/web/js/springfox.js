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
  window.oAuthRedirectUrl = springfox.baseUrl() + '/webjars/springfox-swagger-ui/o2c.html'

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

  $(document).ready(function() {
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

  });

});


