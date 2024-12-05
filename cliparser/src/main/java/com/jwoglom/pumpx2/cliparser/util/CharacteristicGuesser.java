package com.jwoglom.pumpx2.cliparser.util;

import com.google.common.collect.ImmutableSet;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.shared.L;

import java.util.Set;

public class CharacteristicGuesser {
    private static final String TAG = "CharacteristicGuesser";

    public static Characteristic guessBestCharacteristic(String rawHex, int opCode) {
        Set<Characteristic> possibilities = Messages.findPossibleCharacteristicsForOpcode(opCode);
        if (possibilities.size() > 1) {
            String log = "Multiple characteristics possible for opCode: "+opCode+": "+possibilities+" ";
            possibilities = filterKnownPossibilities(rawHex, opCode, possibilities);
            if (possibilities.contains(Characteristic.CONTROL)) {
                log += "Using CONTROL";
                possibilities = ImmutableSet.of(Characteristic.CONTROL);
            } else if (possibilities.contains(Characteristic.AUTHORIZATION)) {
                log += "Using AUTHORIZATION";
                possibilities = ImmutableSet.of(Characteristic.AUTHORIZATION);
            } else if (possibilities.contains(Characteristic.CURRENT_STATUS)) {
                log += "Using CURRENT_STATUS";
                possibilities = ImmutableSet.of(Characteristic.CURRENT_STATUS);
            }
            L.w(TAG, log);
        }
        for (Characteristic c : possibilities) {
            return c;
        }
        return null;
    }


    public static Set<Characteristic> filterKnownPossibilities(String rawHex, int opCode, Set<Characteristic> possibilities) {
        int len = rawHex.length();
        if (
                (opCode == 32 && len == 14) || // ApiVersionRequest with cargo size=2
                        (opCode == 33 && len == 22) || // ApiVersionResponse
                        (opCode == 35 && len == 30) || // CurrentEGVGuiDataResponse
                        (opCode == 37 && len == 22) // InsulinStatusResponse
        ) {
            possibilities = ImmutableSet.of(Characteristic.CURRENT_STATUS);
        } else if (
                (opCode == 37 && len == 148) || // InitiateBolusRequest
                        (opCode == -92 && len == 78) || // SetTempRateRequest
                        (opCode == -91 && len == 70) || // SetTempRateResponse
                        (opCode == -90 && len == 66) || // StopTempRateRequest
                        (opCode == -89 && len == 68) // StopTempRateResponse
        ) {
            possibilities = ImmutableSet.of(Characteristic.CONTROL);
        } else if (
                (opCode == 32 && len == 384) || // Jpake1aRequest
                        (opCode == 33 && len == 384) || // Jpake1aResponse
                        (opCode == 34 && len == 384) || // Jpake1bRequest
                        (opCode == 35 && len == 384) || // Jpake1bResponse
                        (opCode == 36 && len == 384) || // Jpake2Request
                        (opCode == 37 && len == 354) // Jpake2Response
        ) {
            possibilities = ImmutableSet.of(Characteristic.AUTHORIZATION);
        }

        return possibilities;
    }

}
