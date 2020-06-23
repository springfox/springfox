package springfox.documentation.schema.property;

import com.fasterxml.classmate.ResolvedType;

public class PackageNames {
  private PackageNames() {
    throw new UnsupportedOperationException();
  }

  public static String safeGetPackageName(ResolvedType type) {
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

  public static String safeGetPackageName(Class<?> type) {
    if (type != null
        && type.getPackage() != null) {
      return type.getPackage()
          .getName();
    } else {
      return "";
    }
  }
}
