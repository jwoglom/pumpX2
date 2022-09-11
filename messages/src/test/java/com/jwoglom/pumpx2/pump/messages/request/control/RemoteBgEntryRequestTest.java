package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class RemoteBgEntryRequestTest {
    @Test
    public void testRemoteBgEntryRequest_ID10676() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);

        RemoteBgEntryRequest expected = new RemoteBgEntryRequest(180, true, 1200173L, 10676);

        /**
         * 00533353073923851bb40000bfb4    51      response.currentStatus.LastBGResponse   LastBGResponse[bgSource=0,bgTimestamp=461710137,bgValue=180
         * -76, 0, 0, 0, (180)
         * 1,
         * 45, 80, 18, 0 (1200173 = pump time)
         *
         * -76, 41 (10676)
         */

        RemoteBgEntryRequest parsedReq = (RemoteBgEntryRequest) MessageTester.test(
                "023bb63b23b4000000012d501200b4290023851b",
                59,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "013b1f62ef2f006c8a8ea7aeb9203fa4b7eaebd2",
                "003bc0b6800c"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testRemoteBgEntryRequest_ID10677() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200239,timeSinceReset=461710145]
        initPumpState("6VeDeRAL5DCigGw2", 461710145L);

        RemoteBgEntryRequest expected = new RemoteBgEntryRequest(185, true, 1200239L, 10677);


        /**
         * LastBGResponse[bgSource=0,bgTimestamp=461710175,bgValue=185,cargo={95,35,-123,27,-71,0,0}]
         * -71, 0, 0, 0, (185)
         * 1,
         * 111, 80, 18, 0 (1200239 = pump time),
         * -75, 41 (10677)
         */

        RemoteBgEntryRequest parsedReq = (RemoteBgEntryRequest) MessageTester.test(
                "02a9b6a923b9000000016f501200b5294123851b",
                -87,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01a93ade55510ac65b57f647a1899c0a6e4e94bc",
                "00a9d8306d38"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testRemoteBgEntryRequest_ID10678() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1200296,timeSinceReset=461710202]
        initPumpState("6VeDeRAL5DCigGw2", 461710202L);

        RemoteBgEntryRequest expected = new RemoteBgEntryRequest(186, true, 1200239L, 10678);

        RemoteBgEntryRequest parsedReq = (RemoteBgEntryRequest) MessageTester.test(
                "02ceb6ce23ba000000016f501200b6297a23851b",
                -50,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "01ceb50b6d51ca170c5ecf018b37a14a4a05c412",
                "00ceda1ff8b1"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }


    @Test
    public void testRemoteBgEntryRequest_ID10652() throws DecoderException {
        // TimeSinceResetResponse[pumpTime=1079252,timeSinceReset=461589158
        initPumpState("6VeDeRAL5DCigGw2", 461589158L);

        RemoteBgEntryRequest expected = new RemoteBgEntryRequest(142, true, 1079274L, 10652);

        RemoteBgEntryRequest parsedReq = (RemoteBgEntryRequest) MessageTester.test(
                "023cb63c238e00000001ea7710009c29bc4a831b",
                60,
                1,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                "013c1d2edd239e1a8499a6686078565ad1b8acdc",
                "003ca71a3107"
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}