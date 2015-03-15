package springdox.documentation.spi.service.contexts;

import com.google.common.primitives.Ints;
import springdox.documentation.service.ApiDescription;
import springdox.documentation.service.ApiListingReference;
import springdox.documentation.service.Operation;

import java.util.Comparator;

import static com.google.common.base.Strings.*;

public class Orderings {
  private Orderings() {
    throw new UnsupportedOperationException();
  }

  public static Comparator<Operation> nickNameComparator() {
    return new Comparator<Operation>() {
      @Override
      public int compare(Operation first, Operation second) {
        return nullToEmpty(first.getNickname()).compareTo(nullToEmpty(second.getNickname()));
      }
    };
  }

  public static Comparator<Operation> positionComparator() {
    return new Comparator<Operation>() {
      @Override
      public int compare(Operation first, Operation second) {
        return Ints.compare(first.getPosition(), second.getPosition());
      }
    };
  }

  public static Comparator<ApiListingReference> listingReferencePathComparator() {
    return new Comparator<ApiListingReference>() {
      @Override
      public int compare(ApiListingReference first, ApiListingReference second) {
        return first.getPath().compareTo(second.getPath());
      }
    };
  }

  public static Comparator<ApiListingReference> listingPositionComparator() {
    return new Comparator<ApiListingReference>() {
      @Override
      public int compare(ApiListingReference first, ApiListingReference second) {
        return Ints.compare(first.getPosition(), second.getPosition());
      }
    };
  }

  public static Comparator<ApiDescription> apiPathCompatator() {
    return new Comparator<ApiDescription>() {
      @Override
      public int compare(ApiDescription first, ApiDescription second) {
        return first.getPath().compareTo(second.getPath());
      }
    };
  }
}
