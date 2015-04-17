package springfox.documentation.staticdocs;

import org.apache.commons.lang.Validate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Swagger2MarkupDocumentation {

    private static final String OUTPUT_DIR = "io.springfox.staticdocs.outputDir";

    private Swagger2MarkupDocumentation() {}

    /**
     * Converts the Swagger response to AsciiDoc and writes it into the given {@code outputDir}.
     *
     * @param outputDir The directory to which the documentation will be written
     * @return a Mock MVC {@code ResultHandler} that will produce the documentation
     * @see org.springframework.test.web.servlet.MockMvc#perform(org.springframework.test.web.servlet.RequestBuilder)
     * @see org.springframework.test.web.servlet.ResultActions#andDo(org.springframework.test.web.servlet.ResultHandler)
     */
    public static Swagger2MarkupResultHandler document(String outputDir) {
        return new Swagger2MarkupResultHandler(new DocumentationProperties().getOutputDir() + "/" + outputDir);
    }

    static class DocumentationProperties {
        private final Properties properties = new Properties();

        DocumentationProperties() {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(
                    "documentation.properties");
            if (stream != null) {
                try {
                    this.properties.load(stream);
                }
                catch (IOException ex) {
                    throw new IllegalStateException(
                            "Failed to read documentation.properties", ex);
                }
                finally {
                    try {
                        stream.close();
                    }
                    catch (IOException e) {
                        // Continue
                    }
                }
            }
            this.properties.putAll(System.getProperties());
        }

        String getOutputDir() {
            String outputDir = this.properties
                    .getProperty(OUTPUT_DIR);
            Validate.notEmpty(outputDir, "System property '" + OUTPUT_DIR + "' must not be empty!");
            return outputDir;
        }
    }
}
