package springfox.documentation.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class Encoding {
    private final String propertyRef;
    private final String contentType;
    private final ParameterStyle style;
    private final Boolean explode;
    private final Boolean allowReserved;
    private final Set<Header> headers = new HashSet<>();
    private final List<VendorExtension> extensions = new ArrayList<>();

    public Encoding(
        String propertyRef,
        String contentType,
        ParameterStyle style,
        Boolean explode,
        Boolean allowReserved,
        Set<Header> headers,
        Collection<VendorExtension> vendorExtensions) {
        this.contentType = contentType;
        this.style = style;
        this.explode = explode;
        this.allowReserved = allowReserved;
        this.propertyRef = propertyRef;
        this.headers.addAll(headers);
        this.extensions.addAll(vendorExtensions);
    }

    public String getContentType() {
        return contentType;
    }

    public ParameterStyle getStyle() {
        return style;
    }

    public Boolean getExplode() {
        return explode;
    }

    public Boolean getAllowReserved() {
        return allowReserved;
    }

    public Collection<Header> getHeaders() {
        return headers;
    }

    public Collection<VendorExtension> getExtensions() {
        return extensions;
    }

    public String getPropertyRef() {
        return propertyRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Encoding encoding = (Encoding) o;
        return
            Objects.equals(propertyRef, encoding.propertyRef) &&
            Objects.equals(contentType, encoding.contentType) &&
            style == encoding.style &&
            Objects.equals(explode, encoding.explode) &&
            Objects.equals(allowReserved, encoding.allowReserved) &&
            Objects.equals(headers, encoding.headers) &&
            Objects.equals(extensions, encoding.extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyRef, contentType, style, explode, allowReserved, headers, extensions);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Encoding.class.getSimpleName() + "[", "]")
            .add("contentType='" + contentType + "'")
            .add("style=" + style)
            .add("explode=" + explode)
            .add("allowReserved=" + allowReserved)
            .add("headers=" + headers)
            .add("extensions=" + extensions)
            .toString();
    }
}
