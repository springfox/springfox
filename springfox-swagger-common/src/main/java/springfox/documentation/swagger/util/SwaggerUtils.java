package springfox.documentation.swagger.util;

import io.swagger.models.Path;
import io.swagger.models.Swagger;
import org.springframework.util.StringUtils;
import springfox.documentation.swagger.web.SwaggerResource;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenyicheng
 * @version 1.0
 * @since 2019-08-18
 */
public abstract class SwaggerUtils {

    public final static String METHOD_ROUTER = "path";

    public static Swagger filter(HttpServletRequest request, Swagger swagger) {
        String path = request.getParameter(METHOD_ROUTER);
        if (StringUtils.hasLength(path)) {
            //Map<String, Path> pathAll = swagger.getPaths();
            Path pathObj = swagger.getPath(path);

            Map<String, Path> pathMap = new HashMap<String, Path>();
            pathMap.put(path, pathObj);
            swagger.setPaths(pathMap);
        }

        return swagger;
    }


    public static List<SwaggerResource> queryPropagation(HttpServletRequest request, List<SwaggerResource> resources) {
        String query = request.getQueryString();
        if (StringUtils.hasLength(query)) {
            for (SwaggerResource resource : resources) {
                String location = resource.getLocation();
                URI uri = URI.create(location);
                String rawQuery = uri.getRawQuery();
                if (StringUtils.hasLength(rawQuery)) {
                    location = location + "&" + query;
                } else {
                    location = location + "?" + query;
                }
                resource.setLocation(location);
            }
        }
        return resources;
    }


}
