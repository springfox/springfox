package springfox.documentation.uploader.annotations;

import org.springframework.context.annotation.Import;
import springfox.documentation.uploader.FileUploaderBeanConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({FileUploaderBeanConfiguration.class})
public @interface EnableSwaggerUpload {

}
