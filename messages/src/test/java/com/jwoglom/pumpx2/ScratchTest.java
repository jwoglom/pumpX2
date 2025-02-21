package com.jwoglom.pumpx2;

import com.jwoglom.pumpx2.pump.messages.Packetize;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.shared.Hex;

import org.junit.Test;

import java.nio.charset.StandardCharsets;


public class ScratchTest {

    @Test
    public void test() throws Exception {
        byte[] messageData = Hex.decodeHex("4d231a00002e09db1f5c284c424a5db6f2f1b86212a716cded6b04debe");
        // Hex.decodeHex("5c284c424a5db6f2f1b86212a716cded6b04debe");
        byte[] expectedHmac = Bytes.dropFirstN(messageData, messageData.length - 20);

        byte[] authKeyBytes="232826".getBytes(StandardCharsets.UTF_8);
        byte[] derivedSecret = Hex.decodeHex("bf9831c7487330f854a3f1cd2e15fcf94b8f0db8c26ca8e9c5c130876298a82c");

        byte[] byteArray = Bytes.dropLastN(messageData, 20);
        byte[] hmacSha = Packetize.doHmacSha1(byteArray, authKeyBytes);





    }
}
