package com.jwoglom.pumpx2.pump.messages.response;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class PumpGlobalsResponseTest {
    @Test
    public void testPumpGlobalsResponse() throws DecoderException {
        // Control-IQ pump v7.3.1
        PumpGlobalsResponse expected = new PumpGlobalsResponse(
                0, 500, 2000, 0, 0,
                PumpGlobalsResponse.AnnunciationEnum.VIBRATE.id(),
                PumpGlobalsResponse.AnnunciationEnum.AUDIO_LOW.id(),
                PumpGlobalsResponse.AnnunciationEnum.AUDIO_LOW.id(),
                PumpGlobalsResponse.AnnunciationEnum.VIBRATE.id(),
                PumpGlobalsResponse.AnnunciationEnum.VIBRATE.id(),
                PumpGlobalsResponse.AnnunciationEnum.AUDIO_LOW.id(),
                PumpGlobalsResponse.AnnunciationEnum.AUDIO_LOW.id()
        );

        PumpGlobalsResponse parsedRes = (PumpGlobalsResponse) MessageTester.test(
                "000357030e00f401d007000003020203030202050f",
                3,
                2,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );
    }
}
