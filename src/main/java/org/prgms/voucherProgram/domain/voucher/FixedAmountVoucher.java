package org.prgms.voucherProgram.domain.voucher;

import java.util.UUID;

public class FixedAmountVoucher extends Voucher {
    private static final String ERROR_WRONG_DISCOUNT_AMOUNT_MESSAGE = "[ERROR] 올바른 할인금액이 아닙니다.";
    private static final int MIN_AMOUNT = 1;

    private final DiscountAmount discountAmount;

    public FixedAmountVoucher(UUID voucherId, long discountAmount) {
        super(voucherId);
        validateDiscountAmount(discountAmount);
        this.discountAmount = new DiscountAmount(discountAmount);
    }

    private void validateDiscountAmount(long discountAmount) {
        if (isUnderMinAmount(discountAmount)) {
            throw new IllegalArgumentException(ERROR_WRONG_DISCOUNT_AMOUNT_MESSAGE);
        }
    }

    private boolean isUnderMinAmount(long discountAmount) {
        return discountAmount < MIN_AMOUNT;
    }

    @Override
    public long discount(long beforeDiscount) {
        if (discountAmount.isBiggerAmount(beforeDiscount)) {
            return 0;
        }

        return discountAmount.discount(beforeDiscount);
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s", VoucherType.FIXED_AMOUNT, voucherId, discountAmount);
    }
}
