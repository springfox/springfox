package springfox.bean.validators.util;

import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import springfox.bean.validators.plugins.ParameterSizeAnnotationPlugin;
import springfox.documentation.service.AllowableRangeValues;

public class SizeUtil {

	 private static final Logger LOG = LoggerFactory.getLogger(ParameterSizeAnnotationPlugin.class);

	  public static AllowableRangeValues createAllowableValuesFromSizeForStrings(Size size) {
	    LOG.debug("@Size detected: adding MinLength/MaxLength to field");
	    return new AllowableRangeValues(minValue(size), maxValue(size));
	  }

	  private static String minValue(Size size) {
	    return String.valueOf(Math.max(size.min(), 0));
	  }

	  private static String maxValue(Size size) {
	    return String.valueOf(Math.max(0, Math.min(size.max(), Integer.MAX_VALUE)));
	  }
}
