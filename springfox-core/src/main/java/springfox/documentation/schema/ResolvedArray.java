package springfox.documentation.schema;

import com.fasterxml.classmate.ResolvedType;

public class ResolvedArray {
  private final int dimensions;
  private final ResolvedType elementType;

  public ResolvedArray(int dimensions, ResolvedType elementType) {
    this.dimensions = dimensions;
    this.elementType = elementType;
  }

  public int getDimensions() {
    return dimensions;
  }

  public ResolvedType getElementType() {
    return elementType;
  }
}
