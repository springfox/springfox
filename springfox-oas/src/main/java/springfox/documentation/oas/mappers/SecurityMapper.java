package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.mapstruct.Mapper;
import springfox.documentation.service.AuthorizationScope;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", implementationName = "OasSecurityMapperImpl")
public class SecurityMapper {

  public List<SecurityRequirement> mapFrom(Map<String, List<AuthorizationScope>> authorizationScopes) {
    return authorizationScopes.entrySet().stream()
        .map(SecurityMapper::mapFrom)
        .collect(Collectors.toList());
  }

  private static SecurityRequirement mapFrom(Map.Entry<String, List<AuthorizationScope>> entry) {
    SecurityRequirement requirement = new SecurityRequirement();
    return requirement.addList(
        entry.getKey(),
        entry.getValue().stream()
            .map(AuthorizationScope::getScope)
            .collect(Collectors.toList()));
  }
}
