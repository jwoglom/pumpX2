package com.jwoglom.pumpx2.pump.messages.annotations;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.UndefinedMessage;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.bluetooth.CharacteristicUUID;
import com.jwoglom.pumpx2.pump.messages.models.KnownApiVersion;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ApiVersionResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageProps {
    int opCode();
    int size() default 0;
    boolean variableSize() default false;
    boolean stream() default false;
    boolean signed() default false;
    MessageType type();
    Class<? extends Message> response() default UndefinedMessage.class;
    Class<? extends Message> request() default UndefinedMessage.class;
    KnownApiVersion minApi() default KnownApiVersion.API_V2_1;
    Characteristic characteristic() default Characteristic.CURRENT_STATUS;
    boolean modifiesInsulinDelivery() default false;
}
