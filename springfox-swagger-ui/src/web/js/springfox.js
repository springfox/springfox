$(function () {

    var relativeLocation = baseUrl();

    $('#input_baseUrl').hide();
    var swaggerDropdown = $('#select_baseUrl');

    function refresh() {
        window.swaggerUi.headerView.trigger('update-swagger-ui', {
            url: swaggerDropdown.val()
        });
    }

    function baseUrl() {
        var parts = window.location.href.split('/');
        //Walk up the url
        for (i = 0; i < 1; i++) {
            parts.pop();
        }
        return parts.join('/');
    }

    $.getJSON(relativeLocation + "/swagger-resources", function (data) {
        var items = [];
        swaggerDropdown.empty();
        $.each(data, function (i, resource) {
            var option = $('<option></option>').attr("value", relativeLocation + resource.location).text(resource.name + " (" + resource.location + ")");
            swaggerDropdown.append(option);
        });
        refresh();
    });
    swaggerDropdown.change(refresh);
});


