package com.jwoglom.pumpx2.pump.messages.response.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.OpcodeNegative16Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class OpcodeNegative16ResponseTest {
    @Test
    public void testOpcodeNegative16Response() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200173,timeSinceReset=461710079]
        initPumpState("6VeDeRAL5DCigGw2", 461710079L);

        // OpcodeNegative16Request expected = new OpcodeNegative16Request(10676);
        
        OpcodeNegative16Response expected = new OpcodeNegative16Response(
            0
        );

        OpcodeNegative16Response parsedRes = (OpcodeNegative16Response) MessageTester.test(
                "003af13a19003923851b8854859ea0fc17fe530b3c3c4208ae30a555356f0985",
                58,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());
    }
}