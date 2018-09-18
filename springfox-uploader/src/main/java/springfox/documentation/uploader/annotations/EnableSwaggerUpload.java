package springfox.documentation.uploader.annotations;

import org.springframework.context.annotation.Import;
import springfox.documentation.uploader.FileUploaderBeanConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that auto upload should be enabled. This should be applied to a Spring Java config file and should have an
 * accompanying '@Configuration' annotation. Loads all required beans defined in @see FileUploaderBeanConfiguration.
 *
 * @author Esteban Cristóbal Rodríguez
 **/
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({FileUploaderBeanConfiguration.class})
public @interface EnableSwaggerUpload {

}
