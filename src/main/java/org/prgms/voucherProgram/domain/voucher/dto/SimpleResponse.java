package org.prgms.voucherProgram.domain.voucher.dto;

public class SimpleResponse {
    private final String result;

    public SimpleResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
