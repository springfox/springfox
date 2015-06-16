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
        }
    };
    window.springfox = springfox;

    $('#select_baseUrl').change(function() {
        window.swaggerUi.headerView.trigger('update-swagger-ui', {
            url: $('#select_baseUrl').val()
        });
    });

    $(document).ready(function() {
        var relativeLocation = springfox.baseUrl();

        $('#input_baseUrl').hide();

        $.getJSON(relativeLocation + "/swagger-resources", function(data) {

            var $urlDropdown = $('#select_baseUrl');
            $urlDropdown.empty();
            $.each(data, function(i, resource) {
                var option = $('<option></option>')
                        .attr("value", relativeLocation + resource.location)
                        .text(resource.name + " (" + resource.location + ")");
                $urlDropdown.append(option);
            });
            $urlDropdown.change();
        });

    });

});


