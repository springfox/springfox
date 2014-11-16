package com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.util.StringUtils.isEmpty;

public class OperationNicknameReader implements RequestMappingReader {
  @Override
  public void execute(RequestMappingContext context) {
    ApiOperation apiOperationAnnotation =  context.getApiOperationAnnotation();
    String nickname;
    if (null != apiOperationAnnotation && !isEmpty(apiOperationAnnotation.nickname())) {
      nickname = apiOperationAnnotation.nickname();
    } else {
      RequestMethod currentHttpMethod = (RequestMethod) context.get("currentHttpMethod");
      String requestMethod = currentHttpMethod.toString();
      nickname = String.format("%s_%s", requestMethod.toLowerCase(), getMethodSignature(context));
    }
    context.put("nickname", nickname);
  }

  private String getMethodSignature(RequestMappingContext context) {
    String signature = context.getHandlerMethod().toString();
    return signature.replaceAll("\\.", "_").replaceAll(" ", "_");
  }
}
