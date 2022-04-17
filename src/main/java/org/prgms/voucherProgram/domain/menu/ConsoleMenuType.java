package org.prgms.voucherProgram.domain.menu;

import java.util.Arrays;

import org.prgms.voucherProgram.exception.WrongCommandException;

public enum ConsoleMenuType {
    EXIT("exit"),
    CUSTOMER("customer"),
    VOUCHER("voucher");

    private final String command;

    ConsoleMenuType(String command) {
        this.command = command;
    }

    public static ConsoleMenuType from(String command) {
        return Arrays.stream(ConsoleMenuType.values())
            .filter(type -> type.command.equalsIgnoreCase(command))
            .findFirst()
            .orElseThrow(WrongCommandException::new);
    }
}