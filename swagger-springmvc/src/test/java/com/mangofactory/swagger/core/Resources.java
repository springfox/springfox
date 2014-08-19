package com.mangofactory.swagger.core;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.base.Throwables.*;

/**
 * Methods for loading resources.
 */
public class Resources {

  /**
   * Returns the contents of the given resource as a String.
   *
   * @param resourceName The path of the resource to be read. This path must be fully qualied relative to the
   *                     classpath.
   */
  public static String load(String resourceName) {
    return load(resourceName, Resources.class);
  }


  /**
   * Returns the contents of the given resource as a String.
   *
   * @param resourceName The path of the resource to be read. The path can be either fully qualified (if it begins
   *                     with a slash) or relative to the package of the Class argument.
   * @param root         Class whose package will be used to resolve relative resource paths.
   */
  public static String load(String resourceName, Class<?> root) {
    return load(resourceName, root, Charset.defaultCharset());
  }

  /**
   * Returns the contents of the given resource as a String.
   *
   * @param resourceName The path of the resource to be read. The path can be either fully qualified (if it begins
   *                     with a slash) or relative to the package of the Class argument.
   * @param root         Class whose package will be used to resolve relative resource paths.
   * @param charset      The character set of the file.
   * @return
   */
  public static String load(String resourceName, Class<?> root, Charset charset) {
    try {
      URL resource = com.google.common.io.Resources.getResource(root, resourceName);
      return com.google.common.io.Resources.toString(resource, charset);
    } catch (IOException e) {
      throw propagate(e);
    }
  }

  private Resources() {
    throw new UnsupportedOperationException();
  }
}
