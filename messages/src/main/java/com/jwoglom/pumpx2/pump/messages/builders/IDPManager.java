package com.jwoglom.pumpx2.pump.messages.builders;

import com.jwoglom.pumpx2.pump.messages.Message;
import com.jwoglom.pumpx2.pump.messages.models.InsulinUnit;
import com.jwoglom.pumpx2.pump.messages.models.MinsTime;
import com.jwoglom.pumpx2.pump.messages.request.control.CreateIDPRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.DeleteIDPRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.SetActiveIDPRequest;
import com.jwoglom.pumpx2.pump.messages.request.control.SetIDPSegmentRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSegmentRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.IDPSettingsRequest;
import com.jwoglom.pumpx2.pump.messages.request.currentStatus.ProfileStatusRequest;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSegmentResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.IDPSettingsResponse;
import com.jwoglom.pumpx2.pump.messages.response.currentStatus.ProfileStatusResponse;
import com.jwoglom.pumpx2.shared.JavaHelpers;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * IDPManager provides a loose abstraction over processing insulin delivery profile (IDP) response
 * manages, generating the requests necessary to get a full state of the profiles on the pump, and
 * generating request messages in the correct format for making mutations on pump profiles.
 * <p>
 * Instantiate an IDPManager, then periodically call {@link #nextMessages()} and invoke TandemPump.sendCommand()
 * on the returned request messages until {@link #isComplete()}. When a response is received, pass it to IDPManager
 * via call to {@link #processMessage(Message)}. Then {@link #getProfiles()} will return a list of profile
 * objects consisting of segments, and you can perform modifications by performing the returned commands
 * from {@link #createNewProfileMessage}, {@link Profile#deleteProfileMessage()}, {@link Profile#createSegmentMessage}
 * and etc on IDPManager and each Profile.
 * </p>
 */
public class IDPManager {
    /**
     * Wrapper class for an insulin delivery profile consisting of profile settings and all segments.
     */
    public static class Profile {
        private IDPSettingsResponse idpSettingsResponse;
        private List<IDPSegmentResponse> segments = new ArrayList<>();
        boolean isActiveProfile;
        public Profile(IDPSettingsResponse idpSettingsResponse, boolean isActiveProfile) {
            this.idpSettingsResponse = idpSettingsResponse;
            this.isActiveProfile = isActiveProfile;
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

        public Message setActiveProfileMessage() {
            return new SetActiveIDPRequest(getIdpId());
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

        public Message createSegmentMessage(int profileStartTime, int profileBasalRateMilliunits, long profileCarbRatio, int profileTargetBG, int profileISF) {
            return new SetIDPSegmentRequest(getIdpId(), 0 /* ??? */, 0,
                    SetIDPSegmentRequest.IDPSegmentOperation.CREATE_SEGMENT,
                    profileStartTime, profileBasalRateMilliunits, profileCarbRatio, profileTargetBG, profileISF,
                    IDPSegmentResponse.IDPSegmentStatus.toBitmask( // 31
                            IDPSegmentResponse.IDPSegmentStatus.BASAL_RATE,
                            IDPSegmentResponse.IDPSegmentStatus.CARB_RATIO,
                            IDPSegmentResponse.IDPSegmentStatus.TARGET_BG,
                            IDPSegmentResponse.IDPSegmentStatus.CORRECTION_FACTOR,
                            IDPSegmentResponse.IDPSegmentStatus.START_TIME));
        }

        public Message createSegmentMessage(MinsTime profileStartTime, float profileBasalRateUnits, long profileCarbRatio, int profileTargetBG, int profileISF) {
            return createSegmentMessage(profileStartTime.encode(), (int) InsulinUnit.from1To1000(profileBasalRateUnits), profileCarbRatio, profileTargetBG, profileISF);
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

        public String toString() {
            return JavaHelpers.autoToString(this, new HashSet<String>());
        }

        public boolean isActiveProfile() {
            return isActiveProfile;
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

    public IDPManager processMessage(Message message) {
        if (message instanceof ProfileStatusResponse) {
            profileStatusResponse = (ProfileStatusResponse) message;
            profiles.removeIf(p -> !profileStatusResponse.getIdpSlotIds().contains(p.getIdpId()));
        } else if (message instanceof IDPSettingsResponse) {
            int idpId = ((IDPSettingsResponse) message).getIdpId();
            Optional<Profile> profile = profiles.stream().filter(p -> p.getIdpId() == idpId).findFirst();
            if (profile.isPresent()) {
                profile.get().processMessage(message);
            } else {
                int activeSlot = profileStatusResponse.getActiveIdpSlotId();
                int activeIdpId = profileStatusResponse.getIdpSlotIds().get(activeSlot);
                boolean isActiveProfile = idpId == activeIdpId;
                profiles.add(new Profile((IDPSettingsResponse) message, isActiveProfile));
            }
            profiles.sort((a,b) -> profileStatusResponse.getIdpSlotIds().indexOf(a.getIdpId()) - profileStatusResponse.getIdpSlotIds().indexOf(b.getIdpId()));
        } else if (message instanceof IDPSegmentResponse) {
            int idpId = ((IDPSegmentResponse) message).getIdpId();
            Optional<Profile> profile = profiles.stream().filter(p -> p.getIdpId() == idpId).findFirst();
            if (profile.isPresent()) {
                profile.get().processMessage(message);
            }
        }

        return this;
    }

    public static boolean isIDPManagerResponse(Message message) {
        return (message instanceof ProfileStatusResponse) || (message instanceof IDPSettingsResponse) || (message instanceof IDPSegmentResponse);
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

    public Message createNewProfileMessage(String profileName, int defaultCarbRatio, int defaultBasalRateMilliunits, int defaultTargetBG, int defaultISF, int defaultInsulinDuration, boolean profileCarbEntryEnabled) {
        return new CreateIDPRequest(profileName, defaultCarbRatio, defaultBasalRateMilliunits, defaultTargetBG, defaultISF, defaultInsulinDuration, profileCarbEntryEnabled ? 1 : 0);
    }

    /**
     *
     * @param profileName alphanumeric visible name for the profile
     * @param defaultCarbRatio carb ratio for the first profile segment in the profile (at midnight).
     *                         value of '1000' means 1 unit:1 carb. value of '5000' means 1 unit:5 carbs.
     * @param defaultBasalRateUnits basal rate for the first profile segment in units
     * @param defaultTargetBG target BG for first profile segment in mg/dL
     * @param defaultISF insulin sensitivity factor for first profile segment.
     *                   value of '1' means 1 unit:1 mg/dL. value of '5' means 1 unit:5 mg/dL
     * @param defaultInsulinDuration insulin duration via MinsTime constructor
     * @param profileCarbEntryEnabled if carb entry is enabled in the profile
     * @return filled message to send to pump creating the profile
     */
    public Message createNewProfileMessage(String profileName, int defaultCarbRatio, float defaultBasalRateUnits, int defaultTargetBG, int defaultISF, MinsTime defaultInsulinDuration, boolean profileCarbEntryEnabled) {
        return new CreateIDPRequest(profileName, defaultCarbRatio, (int) InsulinUnit.from1To1000(defaultBasalRateUnits), defaultTargetBG, defaultISF, defaultInsulinDuration.encode(), profileCarbEntryEnabled ? 1 : 0);
    }

    @Override
    public String toString() {
        return JavaHelpers.autoToString(this, new HashSet<String>());
    }

}
