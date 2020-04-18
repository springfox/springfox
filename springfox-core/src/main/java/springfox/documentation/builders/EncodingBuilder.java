package springfox.documentation.builders;

import springfox.documentation.service.Encoding;
import springfox.documentation.service.Header;
import springfox.documentation.service.ParameterStyle;
import springfox.documentation.service.VendorExtension;

import java.util.Collection;
import java.util.Set;

public class EncodingBuilder {
  private String propertyRef;
  private String contentType;
  private ParameterStyle style;
  private Boolean explode;
  private Boolean allowReserved;
  private Set<Header> headers;
  private Collection<VendorExtension> vendorExtensions;

  public EncodingBuilder propertyRef(String propertyRef) {
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

  public EncodingBuilder headers(Set<Header> headers) {
    this.headers = headers;
    return this;
  }

  public EncodingBuilder vendorExtensions(Collection<VendorExtension> vendorExtensions) {
    this.vendorExtensions = vendorExtensions;
    return this;
  }

  public Encoding createEncoding() {
    return new Encoding(propertyRef, contentType, style, explode, allowReserved, headers, vendorExtensions);
  }
}