package com.jwoglom.pumpx2.cliparser.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import com.jwoglom.pumpx2.pump.messages.Messages;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.shared.L;

public class CharacteristicGuesser {
    private static final Logger log = LoggerFactory.getLogger(CharacteristicGuesser.class);

    public static Characteristic guessBestCharacteristic(String rawHex, int opCode) {
        Set<Characteristic> possibilities = Messages.findPossibleCharacteristicsForOpcode(opCode);
        if (possibilities.size() > 1) {
            String msg = "Multiple characteristics possible for opCode: "+opCode+": "+possibilities+" ";
            possibilities = filterKnownPossibilities(rawHex, opCode, possibilities);
            if (possibilities.contains(Characteristic.CONTROL)) {
                msg += "Using CONTROL";
                possibilities = Set.of(Characteristic.CONTROL);
            } else if (possibilities.contains(Characteristic.AUTHORIZATION)) {
                msg += "Using AUTHORIZATION";
                possibilities = Set.of(Characteristic.AUTHORIZATION);
            } else if (possibilities.contains(Characteristic.CURRENT_STATUS)) {
                msg += "Using CURRENT_STATUS";
                possibilities = Set.of(Characteristic.CURRENT_STATUS);
            }
            log.warn(msg);
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
            possibilities = Set.of(Characteristic.CURRENT_STATUS);
        } else if (
                (opCode == 37 && len == 148) || // InitiateBolusRequest
                        (opCode == -92 && len == 78) || // SetTempRateRequest
                        (opCode == -91 && len == 70) || // SetTempRateResponse
                        (opCode == -90 && len == 66) || // StopTempRateRequest
                        (opCode == -89 && len == 68) // StopTempRateResponse
        ) {
            possibilities = Set.of(Characteristic.CONTROL);
        } else if (
                (opCode == 32 && len == 384) || // Jpake1aRequest
                        (opCode == 33 && len == 384) || // Jpake1aResponse
                        (opCode == 34 && len == 384) || // Jpake1bRequest
                        (opCode == 35 && len == 384) || // Jpake1bResponse
                        (opCode == 36 && len == 384) || // Jpake2Request
                        (opCode == 37 && len == 354) // Jpake2Response
        ) {
            possibilities = Set.of(Characteristic.AUTHORIZATION);
        }

        return possibilities;
    }

}
