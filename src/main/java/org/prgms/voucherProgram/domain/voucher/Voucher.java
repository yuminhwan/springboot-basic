package org.prgms.voucherProgram.domain.voucher;

import java.io.Serializable;
import java.util.UUID;

public abstract class Voucher implements Serializable {

    protected final UUID voucherId;
    protected final UUID customerId;

    protected Voucher(UUID voucherId, UUID customerId) {
        this.voucherId = voucherId;
        this.customerId = customerId;
    }

    protected Voucher(UUID voucherId) {
        this.voucherId = voucherId;
        this.customerId = null;
    }

    abstract long discount(long beforeDiscount);

    public UUID getVoucherId() {
        return voucherId;
    }
}