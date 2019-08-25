package springfox.documentation.swagger.util;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import org.springframework.util.StringUtils;
import springfox.documentation.swagger.web.SwaggerResource;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;

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

            //filter tags
            filterTags(request, swagger, pathObj);

        }

        return swagger;
    }

    public static Swagger filterTags(HttpServletRequest request, Swagger swagger, Path path) {
        if (path == null) {
            swagger.setTags(null);
            return null;
        }
        Set<Tag> tags = new HashSet<Tag>();
        List<Operation> operations = path.getOperations();
        for (Operation operation : operations) {
            List<String> operationTags = operation.getTags();
            if (operationTags == null) {
                continue;
            }
            for (String operationTag : operationTags) {
                Tag tag = swagger.getTag(operationTag);
                tags.add(tag);
            }

        }
        swagger.setTags(new ArrayList<Tag>(tags));
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
