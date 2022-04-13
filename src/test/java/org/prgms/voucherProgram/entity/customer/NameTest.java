package org.prgms.voucherProgram.entity.customer;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NameTest {

    @DisplayName("이름이 20자를 넘으면 예외를 발생한다.")
    @Test
    void should_ThrowException_When_NameLengthOverThan20() {
        String name = "sksmsdixndxnddlrhfahreowksdfasdsadasdsadsadsadasdsadsadsadasd";

        assertThatThrownBy(() -> new Name(name))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 이름은 20자 이상을 넘을 수 없습니다.");
    }

    @DisplayName("이름이 비어있으면 예외를 발생한다.")
    @Test
    void should_ThrowException_When_NameIsBlank() {
        assertThatThrownBy(() -> new Name("  "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("[ERROR] 이름이 비어있습니다.");
    }

}
