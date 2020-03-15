package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;

class PackageNames {
  private PackageNames() {
    throw new UnsupportedOperationException();
  }

  static String safeGetPackageName(ResolvedType type) {
    if (type != null
        && type.getErasedType() != null
        && type.getErasedType()
               .getPackage() != null) {
      return type.getErasedType()
                 .getPackage()
                 .getName();
    } else {
      return "";
    }
  }
}
