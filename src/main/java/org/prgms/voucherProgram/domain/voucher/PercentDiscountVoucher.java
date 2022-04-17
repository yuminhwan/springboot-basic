package org.prgms.voucherProgram.domain.voucher;

import java.util.UUID;

public class PercentDiscountVoucher extends Voucher {
    private static final String ERROR_WRONG_DISCOUNT_PERCENT_MESSAGE = "[ERROR] 올바른 할인퍼센트가 아닙니다.";
    private static final long MIN_PERCENT = 1;
    private static final long MAX_PERCENT = 100;

    private final long discountPercent;

    public PercentDiscountVoucher(UUID voucherId, long discountPercent) {
        super(voucherId);
        validateDiscountPercent(discountPercent);
        this.discountPercent = discountPercent;
    }

    private void validateDiscountPercent(long discountPercent) {
        if (isWrongPercent(discountPercent)) {
            throw new IllegalArgumentException(ERROR_WRONG_DISCOUNT_PERCENT_MESSAGE);
        }
    }

    private boolean isWrongPercent(long discountPercent) {
        return MAX_PERCENT < discountPercent || discountPercent < MIN_PERCENT;
    }

    @Override
    public long discount(long beforeDiscount) {
        return (long)(beforeDiscount * (1 - (discountPercent / 100.0)));
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%d%%", VoucherType.PERCENT_DISCOUNT, voucherId, discountPercent);
    }
}
