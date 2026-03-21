package com.jwoglom.pumpx2.pump.messages.request.control;

import org.apache.commons.lang3.Validate;
import com.jwoglom.pumpx2.pump.messages.bluetooth.Characteristic;
import com.jwoglom.pumpx2.pump.messages.helpers.Bytes;
import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.MessageType;
import com.jwoglom.pumpx2.pump.messages.annotations.MessageProps;
import com.jwoglom.pumpx2.pump.messages.response.control.DismissNotificationResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.AlertStatusResponse;

@MessageProps(
    opCode=-72,
    size=6,
    type=MessageType.REQUEST,
    characteristic=Characteristic.CONTROL,
    signed=true,
    response=DismissNotificationResponse.class
)
public class DismissNotificationRequest extends Message {

    private long notificationId;
    private int notificationTypeId;
    private NotificationType notificationType;
    private boolean executeExtraAction;

    public DismissNotificationRequest() {
        this.cargo = EMPTY;
    }

    public DismissNotificationRequest(NotificationType notificationType, long notificationId) {
        this(notificationType, notificationId, false);
    }

    public DismissNotificationRequest(NotificationType notificationType, long notificationId, boolean executeExtraAction) {
        parse(buildCargo(notificationId, notificationType.id, executeExtraAction));
    }

    public DismissNotificationRequest(byte[] raw) {
        this.cargo = raw;
        parse(raw);
    }

    public void parse(byte[] raw) {
        raw = this.removeSignedRequestHmacBytes(raw);
        Validate.isTrue(raw.length == props().size());
        this.cargo = raw;
        this.notificationId = Bytes.readUint32(raw, 0);
        this.notificationTypeId = raw[4] & 0xFF;
        this.executeExtraAction = (raw[5] & 0xFF) != 0;
        this.notificationType = getNotificationType();
    }

    public static byte[] buildCargo(long notificationId, int notificationTypeId, boolean executeExtraAction) {
        return Bytes.combine(
                Bytes.toUint32(notificationId),
                Bytes.firstByteLittleEndian(notificationTypeId),
                Bytes.firstByteLittleEndian(executeExtraAction ? 1 : 0)
        );
    }



    public long getNotificationId() {
        return notificationId;
    }

    public int getNotificationTypeId() {
        return notificationTypeId;
    }

    public boolean getExecuteExtraAction() {
        return executeExtraAction;
    }

    public NotificationType getNotificationType() {
        return NotificationType.fromId(notificationTypeId);
    }

    public enum NotificationType {
        REMINDER(0),
        ALERT(1),
        ALARM(2),
        CGM_ALERT(3),

        ;

        private final int id;
        NotificationType(int id) {
            this.id = id;
        }

        public static NotificationType fromId(int id) {
            for (NotificationType type : values()) {
                if (type.id == id) {
                    return type;
                }
            }
            return null;
        }
    }
}