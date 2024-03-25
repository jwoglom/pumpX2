package com.jwoglom.pumpx2.pump.messages.response.authentication;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

public class Jpake4KeyConfirmationResponseTest {
    // ./android-2024-02-29-6char2.csv
    @Test
    public void test_167cargo_5response_split() throws DecoderException {
        //1814	103.269215	c2:48:14:3a:a4:c8 (tslim X2 ***941)	Google_aa:a4:cd (James' Pixel 7a)	ATT	69	00042904320000e3fd32509dfacf470000000000000000d0410856c350ce6b9756e03810c6f99a4a0743160a1fdb0973db2f90bdc9a96ad742	Rcvd Handle Value Notification, Handle: 0x0022 (Unknown)
        Jpake4KeyConfirmationResponse expected = new Jpake4KeyConfirmationResponse(
                0,
                new byte[]{-29,-3,50,80,-99,-6,-49,71},
                new byte[]{0,0,0,0,0,0,0,0},
                new byte[]{-48,65,8,86,-61,80,-50,107,-105,86,-32,56,16,-58,-7,-102,74,7,67,22,10,31,-37,9,115,-37,47,-112,-67,-55,-87,106}
        );

        Jpake4KeyConfirmationResponse parsedReq = (Jpake4KeyConfirmationResponse) MessageTester.test(
                "00042904320000e3fd32509dfacf470000000000000000d0410856c350ce6b9756e03810c6f99a4a0743160a1fdb0973db2f90bdc9a96ad742",
                4,
                4,
                CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(expected.getAppInstanceId(), parsedReq.getAppInstanceId());
        assertHexEquals(expected.getNonce(), parsedReq.getNonce());
        assertHexEquals(expected.getReserved(), parsedReq.getReserved());
        assertHexEquals(expected.getHashDigest(), parsedReq.getHashDigest());
    }
}
