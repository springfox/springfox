package springfox.documentation.builders;

import springfox.documentation.service.Server;
import springfox.documentation.service.ServerVariable;
import springfox.documentation.service.VendorExtension;

import java.util.Collection;
import java.util.List;

public class ServerBuilder {
  private String name;
  private String url;
  private String description;
  private Collection<ServerVariable> variables;
  private List<VendorExtension> extensions;

  public ServerBuilder name(String name) {
    this.name = name;
    return this;
  }

  public ServerBuilder url(String url) {
    this.url = url;
    return this;
  }

  public ServerBuilder description(String description) {
    this.description = description;
    return this;
  }

  public ServerBuilder variables(Collection<ServerVariable> variables) {
    this.variables = variables;
    return this;
  }

  public ServerBuilder extensions(List<VendorExtension> extensions) {
    this.extensions = extensions;
    return this;
  }

  public Server createServer() {
    return new Server(name, url, description, variables, extensions);
  }
}