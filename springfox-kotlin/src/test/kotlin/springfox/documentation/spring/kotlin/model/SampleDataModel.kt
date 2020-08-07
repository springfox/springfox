package springfox.documentation.spring.kotlin.model

import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty


@ApiModel(description = "Sample model")
data class SampleDataModel(
    @ApiModelProperty("ID")
    val id: String,

    @ApiModelProperty("Name")
    val name: String,

    @ApiModelProperty("Age")
    val age: Int
)