package springfox.documentation.spring.web;

import org.springframework.restdocs.templates.TemplateFormat;

public class SpringfoxTemplateFormat implements TemplateFormat {

    private final String name = "springfox";

    private final String fileExtension = "springfox";


    @Override
    public String getId() {
        return name;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }
}
