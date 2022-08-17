package com.jwoglom.pumpx2.pump.messages.request.control;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static com.jwoglom.pumpx2.pump.messages.MessageTester.initPumpState;

import static org.junit.Assert.assertEquals;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.control.BolusPermissionRequest;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

/**
 * BUG: tron.mergeIntoSinglePacket() does not match the packets expected
 *
 * Not sure what the other entries in the cargo are besides the most recent pump_time_since_reset
 */
public class BolusPermissionRequestTest {
    @Test
    public void testBolusPermissionRequest_unknown1() throws DecoderException {
        // PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461457713
        // ./scripts/get-single-opcode.py '0134a234181d47811bc64bcd072fc978e4842046 003426b62ba72612b4f04978ac'

        // runs BEFORE the bolus prompt
        initPumpState("6VeDeRAL5DCigGw2", 461457713);
        BolusPermissionRequest expected = new BolusPermissionRequest(new byte[]{
                29,71,-127,27,-58,75,-51,7,47,-55,120,-28,-124,32,70,38,-74,43,-89,38,18,-76,-16,73
        });


        String[] messages = new String[]{
                "0134a234181d47811bc64bcd072fc978e4842046",
                "003426b62ba72612b4f04978ac"
        };
        BolusPermissionRequest parsedReq = (BolusPermissionRequest) MessageTester.test(
                messages[0],
                52,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                messages[1]
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
    }

    @Test
    public void testBolusPermissionRequest2_unknown2() throws DecoderException {
        // PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461510606
        // ./scripts/get-single-opcode.py '01efa2ef18bc17821b4e1e731847b9d3b2d45332 00ef7f6110aca98e7a3ee69079'

        // thought: 461510606 but changed to 461510588 which is embedded in the request and still works
        initPumpState("6VeDeRAL5DCigGw2", 461510588);
        BolusPermissionRequest expected = new BolusPermissionRequest(new byte[]{
                -68,23,-126,27,78,30,115,24,71,-71,-45,-78,-44,83,50,127,97,16,-84,-87,-114,122,62,-26
        });


        String[] messages = new String[]{
                "01efa2ef18bc17821b4e1e731847b9d3b2d45332",
                "00ef7f6110aca98e7a3ee69079"
        };
        BolusPermissionRequest parsedReq = (BolusPermissionRequest) MessageTester.test(
                messages[0],
                -17, // TODO: wraparound txId
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                messages[1]
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        // does not match the TimeSinceReset??? (gap of 18)
        // this is probably just because the timeSinceReset is one off.
        // assertEquals(461510606, parsedReq.getTimeSinceReset());
        assertEquals(461510588, parsedReq.getTimeSinceReset());
    }

    /*
    0134a23418a64a831b602d09daa68d0806fe4ce80034637bc976656f915150f887	-94	request.control.BolusPermissionRequest
    0034a3341e009c29000000b14a831bdb9371a74b2eb73cb125be67bc82397ebccbb96e7d66	-93	response.control.BolusPermissionResponse
     */
    @Test
    public void testBolusPermissionRequest2_ID10652() throws DecoderException {
        // from much earlier: TimeSinceResetResponse[pumpTime=1079252,timeSinceReset=461589158]
        // PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461589158
        // ./scripts/get-single-opcode.py '0134a23418a64a831b602d09daa68d0806fe4ce8 0034637bc976656f915150f887'

        initPumpState("6VeDeRAL5DCigGw2", 461510606);
        BolusPermissionRequest expected = new BolusPermissionRequest(new byte[]{
                -90,74,-125,27,96,45,9,-38,-90,-115,8,6,-2,76,-24,99,123,-55,118,101,111,-111,81,80
        });
        // 0-3: uint32 timeSinceReset = 461589158
        // 4-24: ???


        String[] messages = new String[]{
                "0134a23418a64a831b602d09daa68d0806fe4ce8",
                "0034637bc976656f915150f887"
        };
        BolusPermissionRequest parsedReq = (BolusPermissionRequest) MessageTester.test(
                messages[0],
                52,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                messages[1]
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(461589158, parsedReq.getTimeSinceReset());

        MessageTester.guessCargo(parsedReq);
    }

    // with empty other cargo: errors returned with additional context:
    // {77,111,-125,27,-70,23,115,84,94,56,40,-107,-22,54,86,99,63,35,77,94,-57,65,61,-120}
    // {-53,86,-125,27,17,122,-97,-77,102,26,73,-80,3,-59,-40,42,90,-34,49,-19,64,-71,-79,123}


    // 0134a23418804b831be6cc73100202962bfbd2e900342a95c2ce3671e84ddd09c9	-94	request.control.BolusPermissionRequest
    //0034a3341e009d290000008b4b831be1fc317efeef79d29d48968bfac0ce1419a6ef9fe205	-93	response.control.BolusPermissionResponse
    @Test
    public void testBolusPermissionRequest3_ID10653() throws DecoderException {
        // from much earlier: TimeSinceResetResponse[pumpTime=1079469,timeSinceReset=461589376]
        // $ PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461589376
        // ./scripts/get-single-opcode.py '0134a23418804b831be6cc73100202962bfbd2e9 00342a95c2ce3671e84ddd09c9'

        initPumpState("6VeDeRAL5DCigGw2", 461589376);
        // BolusPermissionRequest[timeSinceReset=0,cargo={-128,75,-125,27,-26,-52,115,16,2,2,-106,43,-5,-46,-23,42,-107,-62,-50,54,113,-24,77,-35}]
        BolusPermissionRequest expected = new BolusPermissionRequest(new byte[]{
                -128,75,-125,27,-26,-52,115,16,2,2,-106,43,-5,-46,-23,42,-107,-62,-50,54,113,-24,77,-35
        // response: {0,-99,41,0,0,0,-117,75,-125,27,-31,-4,49,126,-2,-17,121,-46,-99,72,-106,-117,-6,-64,-50,20,25,-90,-17,-97}
        });
        // 0-3: uint32 timeSinceReset = 461589376
        // 4-24: ???


        String[] messages = new String[]{
                "0134a23418804b831be6cc73100202962bfbd2e9",
                "00342a95c2ce3671e84ddd09c9"
        };
        BolusPermissionRequest parsedReq = (BolusPermissionRequest) MessageTester.test(
                messages[0],
                52,
                2,
                CharacteristicUUID.CONTROL_CHARACTERISTICS,
                expected,
                messages[1]
        );

        assertHexEquals(expected.getCargo(), parsedReq.getCargo());
        assertEquals(461589376, parsedReq.getTimeSinceReset());

        MessageTester.guessCargo(parsedReq);
    }

    /*
    $ PUMP_AUTHENTICATION_KEY=6VeDeRAL5DCigGw2 PUMP_TIME_SINCE_RESET=461606135 ./scripts/get-single-opcode.py '0134a23418f78c831b2eb357be0238299d11d238 003464a37654dbe85a75ee127b' guesscargo 2>/dev/null

    TimeSinceResetResponse[pumpTime=1096229,timeSinceReset=461606135]
    0134a23418f78c831b2eb357be0238299d11d238003464a37654dbe85a75ee127b	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    0034a3341e009e29000000fd8c831bf13d8ad54ad1d1d18d2cf4c45ffbcb406452b2b9f113	-93	response.control.BolusPermissionResponse

    0138a23818f78c831b041d8672bd6421e4fe743f0038646ab2072bae2a5871fc36	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    0038a3381e009f29000000018d831b1133cc1284ad8166271422d0ac65ad57f9c0e4ecdcb4	-93	response.control.BolusPermissionResponse

    013ca23c18f78c831b5026ff565c1f9c995fd5d9003c307b515fcda236844cf104	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    003ca33c1e00a029000000048d831bb61ed02e3bd811aa01f8cd040fd7572f914e5bcbeb85	-93	response.control.BolusPermissionResponse

    0141a24118f78c831bb9ea5642f184e4d35bfc4000416363bd5af8f46ac922aea8	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    0041a3411e00a129000000078d831bc17a9f80de053fa29e0bcb2ae6f0039ccabd3d4b1af2	-93	response.control.BolusPermissionResponse

    0145a24518f78c831ba8ab19f7812232b1eea13b004570c8a7e8534538fa9a2ec7	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    0045a3451e00a229000000098d831b06b4f5af72320621f279b0a8519af3240b9ed5ea4ea3	-93	response.control.BolusPermissionResponse

    0149a24918f78c831bc15045736a1e81735c1fff0049a8ffba873004ca26216aff	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    0049a3491e00a3290000000c8d831bac5c53a91a61aeb553b07f98964cf4cfce0de26f0541	-93	response.control.BolusPermissionResponse

    014da24d18f78c831ba62747ce58c8f513e0aeba004df96bc4ad488087459650e1	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    004da34d1e00a4290000000f8d831bae7d5928f7fdf8772e58ff2151a7554436be4de16b79	-93	response.control.BolusPermissionResponse

    TimeSinceResetResponse[pumpTime=1096255,timeSinceReset=461606162]
    0153a25318f78c831b6f189910b462647acaf7ab0053bfa7d958e4a11fe2ffba3d	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    0053a3531e00a529000000128d831bb2375536aef90599fd03c4ad80f102d3f4ef0cfbf64e	-93	response.control.BolusPermissionResponse

    0158a25818128d831ba6923a2ea4cf431773e9d800582d115abc75c7a74f1c80b5	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    0058a3581e00a629000000148d831b48c5e8d6a844509bc70e16b7f454c7b93d6c1966afdc	-93	response.control.BolusPermissionResponse

    015ca25c18128d831b15a19b9093c72a900dfac0005c43b111f791fe8b1a47dbf8	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    005ca35c1e00a729000000178d831b7caca0097d81f227c49da17cf2a0c1005724dde92bdf	-93	response.control.BolusPermissionResponse

    0160a26018128d831b2ac82f858c0098e24831d70060cdde761dda58e42b62d932	-94	request.control.BolusPermissionRequest	java.lang.NullPointerException: Cannot invoke "java.util.function.Supplier.get()" because "com.jwoglom.pumpx2.pump.messages.bluetooth.PumpStateSupplier.authenticationKey" is null
    0060a3601e00a8290000001a8d831b8ade5534459bbcc34a989d1cdd28ff3a2074ed76a3c4	-93	response.control.BolusPermissionResponse



     */
}