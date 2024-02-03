package umc.meme.auth.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class UserRequest {
    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelJoinDto {
        private String username;
        private String email;
        private String role;
        private String profileSrc;
        private String nickname;
        private String gender;
        private String skinType;
        private String personalColor;
        private String provider;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtistJoinDto {
        private String username;
        private String email;
        private String role;
        private String profileSrc;
        private String nickname;
        private String provider;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtistExtraDto {
        private Long userId;
        private String profileSrc;
        private String nickname;
        private String gender;
        private String introduction;
        private String workExperience;
        private String makeupLocation;
        private String availableTime;
        private List<String> region;
    }
}
