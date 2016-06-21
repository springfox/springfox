package springfox.documentation.spring.web.dummy.models;

import java.util.List;

public class ModelAttributeComplexTypeExample extends ModelAttributeExample {
	
	private List<FancyPet> fancyPets;
	
	private Category[] categories;
	
	private String[] modelAttributeProperty;

	public List<FancyPet> getFancyPets() {
		return fancyPets;
	}

	public void setFancyPets(List<FancyPet> fancyPets) {
		this.fancyPets = fancyPets;
	}

	public Category[] getCategories() {
		return categories;
	}

	public void setCategories(Category[] categories) {
		this.categories = categories;
	}

	public String[] getModelAttributeProperty() {
		return modelAttributeProperty;
	}

	public void setModelAttributeProperty(String[] modelAttributeProperty) {
		this.modelAttributeProperty = modelAttributeProperty;
	}

}