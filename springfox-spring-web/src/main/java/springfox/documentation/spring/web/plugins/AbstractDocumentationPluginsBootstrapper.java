package springfox.documentation.spring.web.plugins;

import com.fasterxml.classmate.TypeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.PathProvider;
import springfox.documentation.RequestHandler;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.DocumentationPlugin;
import springfox.documentation.spi.service.RequestHandlerCombiner;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.DocumentationContextBuilder;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner;

import java.util.List;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;
import static springfox.documentation.spi.service.contexts.Orderings.pluginOrdering;

public class AbstractDocumentationPluginsBootstrapper {
    private static final Logger log = LoggerFactory.getLogger(DocumentationPluginsBootstrapper.class);
    protected final DocumentationPluginsManager documentationPluginsManager;
    protected final List<RequestHandlerProvider> handlerProviders;
    protected final DocumentationCache scanned;
    protected final ApiDocumentationScanner resourceListing;
    protected final DefaultConfiguration defaultConfiguration;

    private RequestHandlerCombiner combiner;
    private List<AlternateTypeRuleConvention> typeConventions;

    public AbstractDocumentationPluginsBootstrapper(DocumentationPluginsManager documentationPluginsManager,
                                                    List<RequestHandlerProvider> handlerProviders,
                                                    DocumentationCache scanned,
                                                    ApiDocumentationScanner resourceListing,
                                                    Defaults defaults,
                                                    TypeResolver typeResolver,
                                                    PathProvider pathProvider) {
        this.documentationPluginsManager = documentationPluginsManager;
        this.handlerProviders = handlerProviders;
        this.scanned = scanned;
        this.resourceListing = resourceListing;
        this.defaultConfiguration = new DefaultConfiguration(defaults, typeResolver, pathProvider);
    }

    protected void bootstrapDocumentationPlugins() {
        List<DocumentationPlugin> plugins = StreamSupport.stream(documentationPluginsManager.documentationPlugins()
                .spliterator(), false)
                .sorted(pluginOrdering())
                .collect(toList());
        log.info("Found {} custom documentation plugin(s)", plugins.size());
        for (DocumentationPlugin each : plugins) {
            DocumentationType documentationType = each.getDocumentationType();
            if (each.isEnabled()) {
                scanDocumentation(buildContext(each));
            } else {
                log.info("Skipping initializing disabled plugin bean {} v{}",
                        documentationType.getName(), documentationType.getVersion());
            }
        }
    }

    protected DocumentationContext buildContext(DocumentationPlugin each) {
        return each.configure(defaultContextBuilder(each));
    }

    protected void scanDocumentation(DocumentationContext context) {
        try {
            scanned.addDocumentation(resourceListing.scan(context));
        } catch (Exception e) {
            log.error(String.format("Unable to scan documentation context %s", context.getGroupName()), e);
        }
    }

    private DocumentationContextBuilder defaultContextBuilder(DocumentationPlugin plugin) {
        DocumentationType documentationType = plugin.getDocumentationType();
        List<RequestHandler> requestHandlers = handlerProviders.stream()
                .map(handlers())
                .flatMap((handle) -> StreamSupport.stream(handle.spliterator(), false))
                .collect(toList());
        List<AlternateTypeRule> rules = nullToEmptyList(typeConventions).stream()
                .map(AlternateTypeRuleConvention::rules)
                .flatMap((rule) -> StreamSupport.stream(rule.spliterator(), false))
                .collect(toList());
        return documentationPluginsManager
                .createContextBuilder(documentationType, defaultConfiguration)
                .rules(rules)
                .requestHandlers(combiner().combine(requestHandlers));
    }

    private RequestHandlerCombiner combiner() {
        return ofNullable(combiner).orElse(new DefaultRequestHandlerCombiner());
    }

    private Function<RequestHandlerProvider, ? extends Iterable<RequestHandler>> handlers() {
        return (Function<RequestHandlerProvider, Iterable<RequestHandler>>) RequestHandlerProvider::requestHandlers;
    }

    public void setCombiner(RequestHandlerCombiner combiner) {
        this.combiner = combiner;
    }

    public void setTypeConventions(List<AlternateTypeRuleConvention> typeConventions) {
        this.typeConventions = typeConventions;
    }
}
