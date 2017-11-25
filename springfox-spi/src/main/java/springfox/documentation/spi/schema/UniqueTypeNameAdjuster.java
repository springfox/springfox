package springfox.documentation.spi.schema;

import com.fasterxml.classmate.ResolvedType;

public interface UniqueTypeNameAdjuster {

  String get(int modelId);

  void registerType(ResolvedType type, int modelId);

  void setEqualityFor(ResolvedType type, int modelIdOf, int modelIdTo);

}