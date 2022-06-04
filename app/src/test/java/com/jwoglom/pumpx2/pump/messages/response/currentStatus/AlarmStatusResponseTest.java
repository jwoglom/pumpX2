package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlarmStatusResponse;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.math.BigInteger;

public class AlarmStatusResponseTest {
    @Test
    public void testAlarmStatusEmptyResponse() throws DecoderException {
        // empty cargo
        AlarmStatusResponse expected = new AlarmStatusResponse(BigInteger.ZERO);

        AlarmStatusResponse parsedReq = (AlarmStatusResponse) MessageTester.test(
                "000347030800000000000000005721",
                3,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testAlarmStatusPumpReset() throws DecoderException {
        AlarmStatusResponse expected = new AlarmStatusResponse(AlarmStatusResponse.AlarmResponseType.toBitmask(
                AlarmStatusResponse.AlarmResponseType.PUMP_RESET_ALARM,
                AlarmStatusResponse.AlarmResponseType.RESUME_PUMP_ALARM2
        ));

        AlarmStatusResponse parsedReq = (AlarmStatusResponse) MessageTester.test(
                "000c470c0808008000000000001cbd",
                12,
                1,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }
}
