package springfox.documentation.spring.kotlin.controller

import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.spring.kotlin.model.SampleDataModel
import java.util.*

@RestController
@RequestMapping("/kotlin")
class SuspendableController {

    @PostMapping("/suspend/set")
    @Suppress("UNUSED_PARAMETER")
    suspend fun suspendSet(@RequestBody body: SampleDataModel) {
        delay(50)
    }

    @PostMapping("/suspend/get")
    @Deprecated("Some weird reason to avoid")
    suspend fun suspendGet(): SampleDataModel {
        delay(50)
        return SampleDataModel(
            id = UUID.randomUUID().toString(),
            name = "name",
            age = (0..100).random()
        )
    }

}