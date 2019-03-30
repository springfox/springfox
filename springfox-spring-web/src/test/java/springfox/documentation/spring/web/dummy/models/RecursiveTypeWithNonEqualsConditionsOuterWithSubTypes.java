package springfox.documentation.spring.web.dummy.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonSubTypes(@JsonSubTypes.Type(value = RecursiveTypeWithConditions.class, name = "RecursiveTypeWithConditions"))
public class RecursiveTypeWithNonEqualsConditionsOuterWithSubTypes extends RecursiveTypeWithNonEqualsConditionsOuter {

}
