package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.control.CreateIDPRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.DeleteIDPRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.SetIDPSegmentRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSegmentRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ProfileStatusRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSettingsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ProfileStatusResponse;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * IDPManager provides a loose abstraction over processing insulin delivery profile (IDP) response
 * manages, generating the requests necessary to get a full state of the profiles on the pump, and
 * generating request messages in the correct format for making mutations on pump profiles.
 */
public class IDPManager {
    public static class Profile {
        private IDPSettingsResponse idpSettingsResponse;
        private List<IDPSegmentResponse> segments = new ArrayList<>();
        public Profile(IDPSettingsResponse idpSettingsResponse) {
            this.idpSettingsResponse = idpSettingsResponse;
        }

        public boolean isComplete() {
            return segments.size() == idpSettingsResponse.getNumberOfProfileSegments();
        }

        public IDPSettingsResponse getIdpSettingsResponse() {
            return idpSettingsResponse;
        }

        public int getIdpId() {
            return idpSettingsResponse.getIdpId();
        }

        public List<IDPSegmentResponse> getSegments() {
            Validate.isTrue(isComplete());
            return segments;
        }

        public Message deleteProfileMessage() {
            return new DeleteIDPRequest(getIdpId());
        }

        public Message duplicateProfileMessage(String newProfileName) {
            return new CreateIDPRequest(newProfileName, getIdpId());
        }

        public Message deleteSegmentMessage(IDPSegmentResponse segment) {
            Validate.isTrue(segment.getIdpId() == getIdpId());
            Validate.isTrue(segment.getSegmentIndex() != 0, "Deleting the 0th segment index is not supported");
            return new SetIDPSegmentRequest(getIdpId(), 0 /* ??? */, segment.getSegmentIndex(),
                    SetIDPSegmentRequest.IDPSegmentOperation.DELETE_SEGMENT_ID,
                    segment.getProfileStartTime(), segment.getProfileBasalRate(), segment.getProfileCarbRatio(), segment.getProfileTargetBG(), segment.getProfileISF(),
                    segment.getIdpStatusId());
        }

        public Message modifySegmentMessage(IDPSegmentResponse segment) {
            Validate.isTrue(segment.getIdpId() == getIdpId());
            int statusId = 0;
            return new SetIDPSegmentRequest(getIdpId(), 0 /* ??? */, segment.getSegmentIndex(),
                    SetIDPSegmentRequest.IDPSegmentOperation.MODIFY_SEGMENT_ID,
                    segment.getProfileStartTime(), segment.getProfileBasalRate(), segment.getProfileCarbRatio(), segment.getProfileTargetBG(), segment.getProfileISF(),
                    IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                            IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                            IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                            IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                            IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                            IDPSegmentResponse.IDPSegmentStatus.START_TIME));
        }

        public Message createSegmentMessage(int profileStartTime, int profileBasalRate, long profileCarbRatio, int profileTargetBG, int profileISF) {
            return new SetIDPSegmentRequest(getIdpId(), 0 /* ??? */, 0,
                    SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                    profileStartTime, profileBasalRate, profileCarbRatio, profileTargetBG, profileISF,
                    IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                            IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                            IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                            IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                            IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                            IDPSegmentResponse.IDPSegmentStatus.START_TIME));
        }

        private void processMessage(Message message) {
            if (message instanceof IDPSegmentResponse) {
                int idpId = ((IDPSegmentResponse) message).getIdpId();
                Validate.isTrue(idpId == getIdpId());

                int segmentIndex = ((IDPSegmentResponse) message).getSegmentIndex();
                segments.removeIf(s -> segmentIndex == s.getSegmentIndex());
                segments.add((IDPSegmentResponse) message);
                segments.sort(Comparator.comparingInt(IDPSegmentResponse::getSegmentIndex));
            } else if (message instanceof IDPSettingsResponse) {
                int idpId = ((IDPSettingsResponse) message).getIdpId();
                Validate.isTrue(idpId == getIdpId());

                idpSettingsResponse = (IDPSettingsResponse) message;
            }
        }

    }

    private final List<Profile> profiles = new ArrayList<>();
    private ProfileStatusResponse profileStatusResponse;

    public IDPManager() {}
    public IDPManager(ProfileStatusResponse profileStatusResponse) {
        this.profileStatusResponse = profileStatusResponse;
    }

    public List<Message> nextMessages() {
        List<Message> messages = new ArrayList<>();

        if (profileStatusResponse == null) {
            messages.add(new ProfileStatusRequest());
            return messages;
        }

        profileStatusResponse.getIdpSlotIds().forEach(idpSlotId -> {
           Optional<Profile> profile = profiles.stream().filter(p -> p.getIdpId() == idpSlotId).findFirst();

           if (profile.isEmpty()) {
               messages.add(new IDPSettingsRequest(idpSlotId));
           } else {
               int segmentCount = profile.get().getIdpSettingsResponse().getNumberOfProfileSegments();
               if (!profile.get().isComplete()) {
                   for (int i=0; i<segmentCount; i++) {
                       int finalI = i;
                       if (profile.get().segments.stream().noneMatch(s -> s.getSegmentIndex() == finalI)) {
                           messages.add(new IDPSegmentRequest(idpSlotId, i));
                       }
                   }
               }
           }

        });

        return messages;
    }

    public void processMessage(Message message) {
        if (message instanceof ProfileStatusResponse) {
            profileStatusResponse = (ProfileStatusResponse) message;
            profiles.removeIf(p -> !profileStatusResponse.getIdpSlotIds().contains(p.getIdpId()));
        } else if (message instanceof IDPSettingsResponse) {
            int idpId = ((IDPSettingsResponse) message).getIdpId();
            Optional<Profile> profile = profiles.stream().filter(p -> p.getIdpId() == idpId).findFirst();
            if (profile.isPresent()) {
                profile.get().processMessage(message);
            } else {
                profiles.add(new Profile((IDPSettingsResponse) message));
            }
            profiles.sort((a,b) -> profileStatusResponse.getIdpSlotIds().indexOf(a.getIdpId()) - profileStatusResponse.getIdpSlotIds().indexOf(b.getIdpId()));
        } else if (message instanceof IDPSegmentResponse) {
            int idpId = ((IDPSegmentResponse) message).getIdpId();
            Optional<Profile> profile = profiles.stream().filter(p -> p.getIdpId() == idpId).findFirst();
            if (profile.isPresent()) {
                profile.get().processMessage(message);
            }
        }
    }

    public boolean isComplete() {
        return profileStatusResponse != null && profiles.size() == profileStatusResponse.getNumberOfProfiles() && profiles.stream().allMatch(Profile::isComplete);
    }

    public List<Profile> getProfiles() {
        Validate.isTrue(isComplete());
        return profiles;
    }

    public Profile getActiveProfile() {
        Validate.isTrue(isComplete());
        int activeSlot = profileStatusResponse.getActiveIdpSlotId();
        int activeIdpId = profileStatusResponse.getIdpSlotIds().get(activeSlot);
        return getProfileWithIdpId(activeIdpId).orElseThrow();
    }

    public Optional<Profile> getProfileWithIdpId(int idpId) {
        return profiles.stream().filter(p -> p.getIdpId() == idpId).findFirst();
    }

    public void clear() {
        profiles.clear();
        profileStatusResponse = null;
    }

    public Message createNewProfileMessage(String profileName, int firstSegmentProfileCarbRatio, int firstSegmentProfileBasalRate, int firstSegmentProfileTargetBG, int firstSegmentProfileISF, int profileInsulinDuration, int profileCarbEntry) {
        return new CreateIDPRequest(profileName, firstSegmentProfileCarbRatio, firstSegmentProfileBasalRate, firstSegmentProfileTargetBG, firstSegmentProfileISF, profileInsulinDuration, profileCarbEntry);
    }

}
