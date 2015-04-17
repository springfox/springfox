package springfox.documentation.staticdocs;

import io.github.robwin.swagger2markup.Swagger2MarkupConverter;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;


public class Swagger2MarkupResultHandler implements ResultHandler {

    private final String outputDir;

    Swagger2MarkupResultHandler(String outputDir) {
        this.outputDir = outputDir;
    }

    /**
     * Apply the action on the given result.
     *
     * @param result the result of the executed request
     * @throws Exception if a failure occurs
     */
    @Override
    public void handle(MvcResult result) throws Exception {
        String swaggerJson = result.getResponse().getContentAsString();
        Swagger2MarkupConverter.fromString(swaggerJson).build().intoFolder(outputDir);
    }
}
