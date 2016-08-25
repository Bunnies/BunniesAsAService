package io.bunnies.baas.services.v2.responses

class ErrorResponseV2(code: Int, var message: String) {
    var code: String

    init {
        this.code = code.toString()
    }
}
