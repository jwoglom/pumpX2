package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSegmentRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ProfileStatusRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSettingsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ProfileStatusResponse;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

/**
 * A builder which simplifies the logic for getting all insulin delivery profiles and segments.
 * An IDP is a profile selected in the pump. The IDP contained within slot 0 is the current profile
 * being used for active insulin delivery. The ID contained within the slot is then used to request
 * details on that insulin profile, which contains segments for a period of time in which given
 * insulin delivery options are used.
 *
 * Example usage:
 * <pre>
 *   InsulinDeliveryProfileRequestBuilder builder = new InsulinDeliveryProfileRequestBuilder();
 *   request = builder.nextRequest();
 *   pump.sendCommand(peripheral, request.get());
 *
 *   // Inside onRecieveMessage callback...
 *   builder.processResponse(responseMessage);
 *   request = builder.nextRequest();
 *   if (request.isPresent()) {
 *       pump.sendCommand(peripheral, request.get());
 *   }
 * </pre>
 *
 * An initial {@link ProfileStatusRequest}, followed by {@link IDPSettingsRequest} and {@link IDPSegmentRequest} with
 * requisite parameters in order to capture the entire profile state will be returned by nextRequest()
 * so long as you call processResponse() on the returned responses from the pump. nextRequest() will
 * eventually return an empty optional, at which point all profile and segment data has been fetched.
 */
public class InsulinDeliveryProfileRequestBuilder {
    private final Queue<Message> queue;

    public InsulinDeliveryProfileRequestBuilder() {
        queue = new LinkedList<>();
        queue.add(new ProfileStatusRequest());
    }

    public Optional<Message> nextRequest() {
        if (queue.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(queue.poll());
    }

    public void processResponse(Message responseMessage) {
        if (responseMessage instanceof ProfileStatusResponse) {
            ProfileStatusResponse profiles = (ProfileStatusResponse) responseMessage;
            profiles.getIdpSlotIds().forEach(idpId -> queue(new IDPSettingsRequest(idpId)));
        } else if (responseMessage instanceof IDPSettingsResponse) {
            IDPSettingsResponse settings = (IDPSettingsResponse) responseMessage;
            for (int i=1; i<= settings.getNumberOfProfileSegments(); i++) {
                queue(new IDPSegmentRequest(settings.getIdpId(), i));
            }
        }
    }

    private void queue(Message requestMessage) {
        this.queue.add(requestMessage);
    }
}
