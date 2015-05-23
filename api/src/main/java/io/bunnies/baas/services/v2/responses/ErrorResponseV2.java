package io.bunnies.baas.services.v2.responses;

public class ErrorResponseV2 {
    public String code;
    public String message;

    public ErrorResponseV2(int code, String message) {
        this.code = String.valueOf(code);
        this.message = message;
    }
}
