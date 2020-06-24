package springfox.documentation.builders;

import springfox.documentation.service.Encoding;
import springfox.documentation.service.Header;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.VendorExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static springfox.documentation.builders.BuilderDefaults.*;

public class EncodingBuilder {
  private String propertyRef;
  private String contentType;
  private ParameterStyle style;
  private Boolean explode;
  private Boolean allowReserved;
  private final Set<Header> headers = new HashSet<>();
  private final List<VendorExtension> vendorExtensions = new ArrayList<>();

  EncodingBuilder propertyRef(String propertyRef) {
    this.propertyRef = propertyRef;
    return this;
  }

  public EncodingBuilder contentType(String contentType) {
    this.contentType = contentType;
    return this;
  }

  public EncodingBuilder style(ParameterStyle style) {
    this.style = style;
    return this;
  }

  public EncodingBuilder explode(Boolean explode) {
    this.explode = explode;
    return this;
  }

  public EncodingBuilder allowReserved(Boolean allowReserved) {
    this.allowReserved = allowReserved;
    return this;
  }

  public EncodingBuilder headers(Collection<Header> headers) {
    this.headers.addAll(nullToEmptyList(headers));
    return this;
  }

  public EncodingBuilder vendorExtensions(Collection<VendorExtension> vendorExtensions) {
    this.vendorExtensions.addAll(vendorExtensions);
    return this;
  }

  public Encoding build() {
    if (hasOnlyPropertyRef()) {
      return null;
    }
    return new Encoding(propertyRef, contentType, style, explode, allowReserved, headers, vendorExtensions);
  }

  private boolean hasOnlyPropertyRef() {
    return contentType == null
        && style == null
        && explode == null
        && allowReserved == null
        && headers.isEmpty()
        && vendorExtensions.isEmpty();
  }

  public EncodingBuilder copyOf(Encoding other) {
    if (other != null) {
      this.propertyRef(other.getPropertyRef())
          .contentType(other.getContentType())
          .allowReserved(other.getAllowReserved())
          .explode(other.getExplode())
          .headers(other.getHeaders())
          .style(other.getStyle())
          .vendorExtensions(other.getExtensions());
    }
    return this;
  }
}