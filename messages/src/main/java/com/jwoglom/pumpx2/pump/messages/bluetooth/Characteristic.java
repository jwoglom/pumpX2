package com.jwoglom.pumpx2.pump.messages.bluetooth;


import static com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID.AUTHORIZATION_CHARACTERISTICS;
import static com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID.CONTROL_CHARACTERISTICS;
import static com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID.CONTROL_STREAM_CHARACTERISTICS;
import static com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID.CURRENT_STATUS_CHARACTERISTICS;
import static com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID.HISTORY_LOG_CHARACTERISTICS;

import java.util.UUID;

/**
 * Enum version of CharacteristicUUID
 *
 * TODO: refactor all uses to this class
 */
public enum Characteristic {
    CURRENT_STATUS(CURRENT_STATUS_CHARACTERISTICS),
    HISTORY_LOG(HISTORY_LOG_CHARACTERISTICS),
    AUTHORIZATION(AUTHORIZATION_CHARACTERISTICS),
    CONTROL(CONTROL_CHARACTERISTICS),
    CONTROL_STREAM(CONTROL_STREAM_CHARACTERISTICS),
    ;

    private final UUID uuid;
    Characteristic(UUID uuid) {
        this.uuid = uuid;
    }

    public static Characteristic of(UUID uuid) {
        for (Characteristic c : values()) {
            if (c.uuid.toString().equals(uuid.toString())) {
                return c;
            }
        }
        return null;
    }
}