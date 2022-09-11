package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.RemoteCarbEntryRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class RemoteCarbEntryRequestTest {
    // test for unknown opcode -16
//    @Test
//    public void testRemoteCarbEntryRequest() throws DecoderException {
//        // empty cargo
//        RemoteCarbEntryRequest expected = new RemoteCarbEntryRequest(0, 0, 0);
//
//        RemoteCarbEntryRequest parsedReq = (RemoteCarbEntryRequest) MessageTester.test(
//                "013af03a1cb42900000023851b3c39b657fe391e",
//                58,
//                2,
//                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
//                expected,
//                "003ac14d83666a2599ae79a30e5d9b459a"
//        );
//
//        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
//    }


    // test for unknown opcode -14
//    @Test
//    public void testRemoteCarbEntryRequest() throws DecoderException {
//        initPumpState("bJVYJq9jdtjxPcYa", 463684080L);
//        // empty cargo
//        RemoteCarbEntryRequest expected = new RemoteCarbEntryRequest(0, 0, 0);
//
//        RemoteCarbEntryRequest parsedReq = (RemoteCarbEntryRequest) MessageTester.test(
//                "025df25d210f0001f96e3000b829f041a31b6a92",
//                93,
//                2,
//                CharacteristicUUID.CONTROL_CHARACTERISTICS,
//                expected,
//                "015db579d5c1eb18ccf97d87de9f6330512bf2f6",
//                "005dccfa"
//        );
//
//        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
//    }
}