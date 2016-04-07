package springfox.documentation.swagger2.mappers

import org.mapstruct.factory.Mappers

trait MapperSupport {
  ModelMapper model() {
    Mappers.getMapper(ModelMapper)
  }

  ParameterMapper parameter() {
    Mappers.getMapper(ParameterMapper)
  }

  SecurityMapper security() {
    Mappers.getMapper(SecurityMapper)
  }

  LicenseMapper license() {
    Mappers.getMapper(LicenseMapper)
  }

  VendorExtensionsMapper vendorExtensions() {
    Mappers.getMapper(VendorExtensionsMapper)
  }

  ServiceModelToSwagger2Mapper swagger2Mapper() {
    def swagger2 = Mappers.getMapper(ServiceModelToSwagger2Mapper)
    swagger2.modelMapper = model()
    swagger2.parameterMapper = parameter()
    swagger2.securityMapper = security()
    swagger2.licenseMapper = license()
    swagger2.vendorExtensionsMapper = vendorExtensions()
    swagger2
  }

}