package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSegmentRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSettingsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ProfileStatusResponse;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    private List<Profile> profiles = new ArrayList<>();
    private ProfileStatusResponse profileStatusResponse;

    public IDPManager(ProfileStatusResponse profileStatusResponse) {
        this.profileStatusResponse = profileStatusResponse;
    }

    public List<Message> nextMessages() {
        List<Message> messages = new ArrayList<>();
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
        return profiles.size() == profileStatusResponse.getNumberOfProfiles() && profiles.stream().allMatch(Profile::isComplete);
    }

    public List<Profile> getProfiles() {
        Validate.isTrue(isComplete());
        return profiles;
    }

    public Profile getActiveProfile() {
        Validate.isTrue(isComplete());
        Optional<Profile> profile = profiles.stream().filter(p -> p.getIdpId() == profileStatusResponse.getActiveIdpSlotId()).findFirst();
        return profile.orElseThrow();
    }

}
