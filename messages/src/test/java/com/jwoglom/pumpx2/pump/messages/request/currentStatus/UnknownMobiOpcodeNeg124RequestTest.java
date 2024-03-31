package com.jwoglom.pumpx2.pump.messages.request.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.PacketArrayList;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.UnknownMobiOpcodeNeg124Request;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

//@Ignore("signed messages without key")
public class UnknownMobiOpcodeNeg124RequestTest {
    @Test
    public void testUnknownMobiOpcodeNeg124Request_a() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512442620,pumpTimeSinceReset=1905190,cargo={-4,64,-117,30,38,18,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905190L);
        UnknownMobiOpcodeNeg124Request expected = new UnknownMobiOpcodeNeg124Request(
            // just encryption bytes
            new byte[]{98,-107,-117,30,-87,86,-101,121,-68,18,80,42,-46,-128,7,-90,-111,104,84,-75,24,-62,30,25}
        );

        UnknownMobiOpcodeNeg124Request parsedReq = (UnknownMobiOpcodeNeg124Request) MessageTester.test(
                // Untitled_1_Live_-_Humans_iPhone_non-decoded
                "01ae84ae1862958b1ea9569b79bc12502ad28007",
                -82,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00aea6916854b518c21e1961fb"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testUnknownMobiOpcodeNeg124Request_b() throws DecoderException {
        // TimeSinceResetResponse[currentTime=512442755,pumpTimeSinceReset=1905325,cargo={-125,65,-117,30,-83,18,29,0}]
        initPumpState(PacketArrayList.IGNORE_INVALID_HMAC, 1905325L);
        UnknownMobiOpcodeNeg124Request expected = new UnknownMobiOpcodeNeg124Request(
                // just encryption bytes
                 new byte[]{-23,-107,-117,30,15,-11,-60,-106,104,46,112,-124,-66,45,-84,-66,24,-94,-2,1,-86,98,-120,-56}
        );

        UnknownMobiOpcodeNeg124Request parsedReq = (UnknownMobiOpcodeNeg124Request) MessageTester.test(
                // Untitled_1_Live_-_Humans_iPhone_non-decoded
                "01c584c518e9958b1e0ff5c496682e7084be2dac",
                -59,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "00c5be18a2fe01aa6288c8e065"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}