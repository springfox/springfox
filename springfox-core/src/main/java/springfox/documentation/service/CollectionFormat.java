package springfox.documentation.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class CollectionFormat {
  public static final CollectionFormat NONE = null;
  public static final CollectionFormat CSV = new CollectionFormat("csv", "comma separated values foo,bar");
  public static final CollectionFormat SSV = new CollectionFormat("ssv", "space separated values foo bar");
  public static final CollectionFormat TSV = new CollectionFormat("tsv", "tab separated values foo\tbar");
  public static final CollectionFormat PIPES = new CollectionFormat("pipes", "pipe separated values foo|bar");
  public static final CollectionFormat MULTI =
      new CollectionFormat("multi", "corresponds to multiple parameter instances instead " +
          "of multiple values for a single insta = new CollectionFormatnce foo=bar&foo=baz. " +
          "This is valid only for parameters in \"query\" or \"formData\"");

  private static final Map<String, CollectionFormat> KNOWN_FORMATS = new HashMap<>();

  static {
    KNOWN_FORMATS.put(CSV.type, CSV);
    KNOWN_FORMATS.put(SSV.type, SSV);
    KNOWN_FORMATS.put(TSV.type, TSV);
    KNOWN_FORMATS.put(PIPES.type, PIPES);
    KNOWN_FORMATS.put(MULTI.type, MULTI);
  };

  private final String type;
  private final String description;

  CollectionFormat(String type, String description) {

    this.type = type;
    this.description = description;
  }

  public String getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CollectionFormat that = (CollectionFormat) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", CollectionFormat.class.getSimpleName() + "[", "]")
        .add("type='" + type + "'")
        .add("description='" + description + "'")
        .toString();
  }

  public static Optional<CollectionFormat> convert(String format) {
    return Optional.ofNullable(KNOWN_FORMATS.get(format));
  }
}
