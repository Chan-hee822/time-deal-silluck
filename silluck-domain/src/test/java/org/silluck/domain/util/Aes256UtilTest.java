package org.silluck.domain.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Aes256UtilTest {

    @Test
    void encrypt() {
        String encrypt = Aes256Util.encrypt("hello nice meet you");
        assertEquals(Aes256Util.decrypt(encrypt), "hello nice meet you");
    }
}