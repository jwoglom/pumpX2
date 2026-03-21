package com.jwoglom.pumpx2.pump.messages.request.control;

import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.control.StreamDataPreflightResponse;

import java.util.Arrays;

/**
 * Sends a preflight request before streaming CGM data.
 *
 * Cargo layout (3 + N bytes):
 *   raw[0]:   streamType (uint8, PreflightStreamType ordinal)
 *   raw[1-2]: length     (uint16 LE)
 *   raw[3..]: hmac       (variable length byte array)
 *
 * Derived from the decompiled Tandem Mobi Android app:
 *   StreamDataPreflightRequest$StreamDataPreflightRequestCargo
 */
@MessageProps(
    opCode=-126,
    size=3,
    variableSize=true,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    response=StreamDataPreflightResponse.class,
    minApi=KnownApiVersion.API_FUTURE,
    signed=true
)
public class StreamDataPreflightRequest extends Message {

    private int streamType;
    private int length;
    private byte[] hmac;

    public StreamDataPreflightRequest() {}

    public StreamDataPreflightRequest(int streamType, int length, byte[] hmac) {
        this.cargo = buildCargo(streamType, length, hmac);
        this.streamType = streamType;
        this.length = length;
        this.hmac = hmac;
    }

    public void parse(byte[] raw) {
        raw = removeSignedRequestHmacBytes(raw);
        this.cargo = raw;
        this.streamType = raw[0] & 0xFF;
        this.length = Bytes.readShort(raw, 1);
        this.hmac = Arrays.copyOfRange(raw, 3, raw.length);
    }

    public static byte[] buildCargo(int streamType, int length, byte[] hmac) {
        return Bytes.combine(
            new byte[]{ (byte) streamType },
            Bytes.firstTwoBytesLittleEndian(length),
            hmac
        );
    }

    public int getStreamType() {
        return streamType;
    }

    public int getLength() {
        return length;
    }

    public byte[] getHmac() {
        return hmac;
    }
}
