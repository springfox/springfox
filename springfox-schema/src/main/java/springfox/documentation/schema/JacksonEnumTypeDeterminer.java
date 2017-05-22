package springfox.documentation.schema;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.stereotype.Component;
import springfox.documentation.spi.schema.EnumTypeDeterminer;

@Component
public class JacksonEnumTypeDeterminer implements EnumTypeDeterminer{
    public boolean isEnum(Class<?> type) {
        if(type.isEnum()){
            JsonFormat annotation = type.getAnnotation(JsonFormat.class);
            if(annotation!=null) {
                return !annotation.shape().equals(JsonFormat.Shape.OBJECT);
            }else{
                return true;
            }
        }else{
            return false;
        }
    }
}
