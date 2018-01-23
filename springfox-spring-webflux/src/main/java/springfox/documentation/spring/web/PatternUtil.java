package springfox.documentation.spring.web;

import org.springframework.web.util.pattern.PathPattern;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PatternUtil {

    public static Set<String> toListString(Set<PathPattern> patterns) {
        Set<String> paths = new HashSet<String>();
        for (PathPattern p: patterns) {
            paths.add(p.getPatternString());
        }
        return paths;
    }
}
