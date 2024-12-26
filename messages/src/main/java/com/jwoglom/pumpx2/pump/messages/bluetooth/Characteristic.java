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
    /**
     * 7B83FFF6-9F77-4E5C-8064-AAE2C24838B9
     */
    CURRENT_STATUS(CURRENT_STATUS_CHARACTERISTICS),
    /**
     * 7B83FFF8-9F77-4E5C-8064-AAE2C24838B9
     */
    HISTORY_LOG(HISTORY_LOG_CHARACTERISTICS),
    /**
     * 7B83FFF9-9F77-4E5C-8064-AAE2C24838B9
     */
    AUTHORIZATION(AUTHORIZATION_CHARACTERISTICS),
    /**
     * 7B83FFFC-9F77-4E5C-8064-AAE2C24838B9
     */
    CONTROL(CONTROL_CHARACTERISTICS),
    /**
     * 7B83FFFD-9F77-4E5C-8064-AAE2C24838B9
     */
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

    public UUID getUuid() {
        return uuid;
    }
}