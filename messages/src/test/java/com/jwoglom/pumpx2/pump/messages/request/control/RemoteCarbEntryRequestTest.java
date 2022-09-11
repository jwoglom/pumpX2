package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class RemoteCarbEntryRequestTest {
    @Test
    public void testOpcodeNegative14Request_ID10677() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);

        RemoteCarbEntryRequest expected = new RemoteCarbEntryRequest(
                5, 1, 1200239L, 10677);

        RemoteCarbEntryRequest parsedReq = (RemoteCarbEntryRequest) MessageTester.test(
                "02aaf2aa210500016f501200b5294123851bf324",
                -86,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01aaa4f96f10fe37167c6d90d2802fda729c1e0e",
                "00aa0288"
        );
        // [5,0,1,
        // 111,80,18,0, = pumpTime
        // -75,41] = bolusId

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testOpcodeNegative14Request_ID10678() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200296,timeSinceReset=461710202]
        initPumpState("6VeDeRAL5DCigGw2", 461710202L);

        RemoteCarbEntryRequest expected = new RemoteCarbEntryRequest(
                3, 1, 1200239L, 10678);

        RemoteCarbEntryRequest parsedReq = (RemoteCarbEntryRequest) MessageTester.test(
                "02cff2cf210300016f501200b6297a23851bc88f",
                -49,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01cfdcc573801e4a91567fe0219a9275211f7f17",
                "00cfa162"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testRemoteCarbEntryRequest_ID10653_011u_11g_carbs_161mgdl_013u_iob() throws DecoderException {
        // $ PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461589420
        initPumpState("6VeDeRAL5DCigGw2", 461589420L);

        RemoteCarbEntryRequest expected = new RemoteCarbEntryRequest(
                11, 1, 1079469L, 10653);

        RemoteCarbEntryRequest parsedReq = (RemoteCarbEntryRequest) MessageTester.test(
                "0238f238210b0001ad7810009d29ac4b831b5e16",
                56,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "013890d5f066c547ed4e3e883476f805abc0ddf9",
                "00383346"
        );
        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}