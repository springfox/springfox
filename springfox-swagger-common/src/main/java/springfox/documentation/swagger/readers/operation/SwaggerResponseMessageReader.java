package springfox.documentation.swagger.readers.operation;

import com.google.common.base.Optional;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.annotations.Annotations;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Set;

import static com.google.common.collect.Sets.*;

@Component
public class SwaggerResponseMessageReader implements OperationBuilderPlugin {

  @Override
  public void apply(OperationContext context) {
    HandlerMethod handlerMethod = context.getHandlerMethod();
    context.operationBuilder()
            .responseMessages(read(handlerMethod));

  }

  @Override
  public boolean supports(DocumentationType delimiter) {
    return SwaggerPluginSupport.pluginDoesApply(delimiter);
  }

  protected Set<ResponseMessage> read(HandlerMethod handlerMethod) {
    Optional<ApiResponses> apiResponsesOptional = Annotations.findApiResponsesAnnotations(handlerMethod.getMethod());
    Set<ResponseMessage> responseMessages = newHashSet();
    if (apiResponsesOptional.isPresent()) {
      ApiResponse[] apiResponseAnnotations = apiResponsesOptional.get().value();
      for (ApiResponse apiResponse : apiResponseAnnotations) {
        String overrideTypeName = overrideTypeName(apiResponse);

        responseMessages.add(new ResponseMessageBuilder()
                .code(apiResponse.code())
                .message(apiResponse.message())
                .responseModel(new ModelRef(overrideTypeName))
                .build());
      }
    }
    return responseMessages;
  }


  private String overrideTypeName(ApiResponse apiResponse) {
    if (apiResponse.response() != null) {
      return apiResponse.response().getSimpleName();
    }
    return "";//TODO: May not be correct
  }

}
