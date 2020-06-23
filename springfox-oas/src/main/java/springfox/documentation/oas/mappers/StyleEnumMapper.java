package springfox.documentation.oas.mappers;

import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.Encoding;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import springfox.documentation.service.ParameterStyle;

@Mapper(componentModel = "spring")
@Named("StyleEnumSelector")
public abstract class StyleEnumMapper {

  @Named("HeaderStyleEnum")
  @ValueMappings({
      @ValueMapping(source = "DEFAULT", target = MappingConstants.NULL),
      @ValueMapping(source = "SIMPLE", target = "SIMPLE"),
      @ValueMapping(source = "MATRIX", target = MappingConstants.NULL),
      @ValueMapping(source = "LABEL", target = MappingConstants.NULL),
      @ValueMapping(source = "FORM", target = MappingConstants.NULL),
      @ValueMapping(source = "SPACEDELIMITED", target = MappingConstants.NULL),
      @ValueMapping(source = "PIPEDELIMITED", target = MappingConstants.NULL),
      @ValueMapping(source = "DEEPOBJECT", target = MappingConstants.NULL),
  })
  public abstract Header.StyleEnum headerStyle(ParameterStyle from);

  @Named("EncodingStyleEnum")
  @ValueMappings({
      @ValueMapping(source = "DEFAULT", target = MappingConstants.NULL),
      @ValueMapping(source = "SIMPLE", target = MappingConstants.NULL),
      @ValueMapping(source = "MATRIX", target = MappingConstants.NULL),
      @ValueMapping(source = "LABEL", target = MappingConstants.NULL),
      @ValueMapping(source = "FORM", target = MappingConstants.NULL),
      @ValueMapping(source = "SPACEDELIMITED", target = "SPACE_DELIMITED"),
      @ValueMapping(source = "PIPEDELIMITED", target = "PIPE_DELIMITED"),
      @ValueMapping(source = "DEEPOBJECT", target = "DEEP_OBJECT"),
  })
  public abstract Encoding.StyleEnum encodingStyle(ParameterStyle from);
}
