package com.jwoglom.pumpx2.pump.messages.response.currentStatus;

import static com.jwoglom.pumpx2.pump.messages.MessageTester.assertHexEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.jwoglom.pumpx2.pump.messages.MessageTester;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.CommonSoftwareInfoRequest;
import com.jwoglom.pumpx2.shared.Hex;

import org.apache.commons.codec.DecoderException;
import org.junit.Ignore;
import org.junit.Test;

public class CommonSoftwareInfoResponseTest {
    @Test
    public void testCommonSoftwareInfoResponse() throws DecoderException { 
        CommonSoftwareInfoResponse expected = new CommonSoftwareInfoResponse(
            new byte[]{
                    0,
                    102,
                    99,
                    56,
                    50,
                    101,
                    52,
                    57,
                    53,
                    100,
                    100,
                    101,
                    56,
                    50,
                    102,
                    99,
                    102,
                    0,
                    22,
                    122,
                    15,
                    0,
                    0,
                    0,
                    0,
                    0,
                    102,
                    99,
                    56,
                    50,
                    101,
                    52,
                    57,
                    53,
                    100,
                    100,
                    101,
                    56,
                    50,
                    102,
                    99,
                    102,
                    0,
                    22,
                    122,
                    15,
                    0,
                    0,
                    0,
                    0,
                    0
            }
        );

        CommonSoftwareInfoResponse parsedRes = (CommonSoftwareInfoResponse) MessageTester.test(
                "002f8f2f33006663383265343935646465383266636600167a0f00000000006663383265343935646465383266636600167a0f00000000000457",
                47,
                4,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());        assertMobi770(parsedRes);
    }


    @Test
    public void testCommonSoftwareInfoResponse_MobiV3_6() throws DecoderException {
        CommonSoftwareInfoResponse expected = new CommonSoftwareInfoResponse(
                new byte[]{
                        0,102,99,56,50,101,52,57,53,100,100,101,56,50,102,99,102,0,22,122,15,0,0,0,0,0,102,99,56,50,101,52,57,53,100,100,101,56,50,102,99,102,0,22,122,15,0,0,0,0,0
                }
        );

        CommonSoftwareInfoResponse parsedRes = (CommonSoftwareInfoResponse) MessageTester.test(
                "00398f3933006663383265343935646465383266636600167a0f00000000006663383265343935646465383266636600167a0f0000000000a739",
                57,
                4,
                CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS,
                expected
        );

        assertHexEquals(expected.getCargo(), parsedRes.getCargo());        assertMobi770(parsedRes);
    }

    // Maintainer's Tandem Mobi on Control-IQ 7.7.0.1, MCU type 0 (ARM).
    private static void assertMobi770(CommonSoftwareInfoResponse res) {
        assertEquals(Integer.valueOf(0), res.getMcuTypeId());
        assertEquals(CommonSoftwareInfoRequest.MCUType.ARM, res.getMcuType());
        assertEquals("fc82e495dde82fcf", res.getAppSoftwareVersion());
        assertEquals(1014294L, res.getAppSoftwarePartNumber());
        assertEquals(0L, res.getAppSoftwarePartRevisionNumber());
        assertEquals(0L, res.getAppSoftwarePartDashNumber());
        assertEquals("fc82e495dde82fcf", res.getBootloaderVersion());
        assertEquals(1014294L, res.getBootloaderPartNumber());
        assertEquals(Long.valueOf(0), res.getBootloaderPartRevisionNumber());
        assertNull(res.getBootloaderPartDashNumber());
    }

    @Test
    public void testCommonSoftwareInfoResponse_Mobi7902_bleMcu() throws DecoderException {
        // Maintainer's Tandem Mobi on Control-IQ+ 7.9.0.2, reply to CommonSoftwareInfoRequest(BLE).
        byte[] raw = Hex.decodeHex("016433633931616435346331326232323800b3870f00000000006433633931616435346331326232323800b3870f0000000000");
        CommonSoftwareInfoResponse parsed = new CommonSoftwareInfoResponse(raw);

        assertEquals(Integer.valueOf(1), parsed.getMcuTypeId());
        assertEquals(CommonSoftwareInfoRequest.MCUType.BLE, parsed.getMcuType());
        assertEquals("d3c91ad54c12b228", parsed.getAppSoftwareVersion());
        assertEquals(1017779L, parsed.getAppSoftwarePartNumber());
        assertEquals(0L, parsed.getAppSoftwarePartRevisionNumber());
        assertEquals("d3c91ad54c12b228", parsed.getBootloaderVersion());
        assertEquals(1017779L, parsed.getBootloaderPartNumber());
        assertEquals(Long.valueOf(0), parsed.getBootloaderPartRevisionNumber());

        CommonSoftwareInfoResponse built = new CommonSoftwareInfoResponse(1, "d3c91ad54c12b228", 1017779L, 0L, "d3c91ad54c12b228", 1017779L, 0L);
        assertHexEquals(raw, built.getCargo());
    }

    @Test
    public void testCommonSoftwareInfoResponse_Mobi7901_armMcu() throws DecoderException {
        // Maintainer's Tandem Mobi on Control-IQ+ 7.9.0.1, reply to CommonSoftwareInfoRequest(ARM).
        byte[] raw = Hex.decodeHex("0034323064643130663137343365623765000b830f000000000034323064643130663137343365623765000b830f0000000000");
        CommonSoftwareInfoResponse parsed = new CommonSoftwareInfoResponse(raw);

        assertEquals(CommonSoftwareInfoRequest.MCUType.ARM, parsed.getMcuType());
        assertEquals("420dd10f1743eb7e", parsed.getAppSoftwareVersion());
        assertEquals(1016587L, parsed.getAppSoftwarePartNumber());
        assertEquals("420dd10f1743eb7e", parsed.getBootloaderVersion());
        assertEquals(1016587L, parsed.getBootloaderPartNumber());

        CommonSoftwareInfoResponse built = new CommonSoftwareInfoResponse(0, "420dd10f1743eb7e", 1016587L, 0L, "420dd10f1743eb7e", 1016587L, 0L);
        assertHexEquals(raw, built.getCargo());
    }

    @Test
    public void testCommonSoftwareInfoResponse_x2LayoutRoundTrip() {
        // No t:slim X2 response has been captured; this checks the 60-byte layout the X2 app reads.
        CommonSoftwareInfoResponse built = new CommonSoftwareInfoResponse("0123456789abcdef", 1000001L, 2L, 3L, "fedcba9876543210", 1000004L, 5L, 6L);
        CommonSoftwareInfoResponse parsed = new CommonSoftwareInfoResponse(built.getCargo());

        assertNull(parsed.getMcuTypeId());
        assertEquals("0123456789abcdef", parsed.getAppSoftwareVersion());
        assertEquals(1000001L, parsed.getAppSoftwarePartNumber());
        assertEquals(2L, parsed.getAppSoftwarePartDashNumber());
        assertEquals(3L, parsed.getAppSoftwarePartRevisionNumber());
        assertEquals("fedcba9876543210", parsed.getBootloaderVersion());
        assertEquals(1000004L, parsed.getBootloaderPartNumber());
        assertEquals(Long.valueOf(5), parsed.getBootloaderPartDashNumber());
        assertEquals(Long.valueOf(6), parsed.getBootloaderPartRevisionNumber());
    }
}