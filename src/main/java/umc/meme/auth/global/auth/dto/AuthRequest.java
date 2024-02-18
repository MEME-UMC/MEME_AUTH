package umc.meme.auth.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import umc.meme.auth.global.enums.*;

import java.util.List;
import java.util.Map;

public class AuthRequest {
    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelJoinDto {
        private String id_token;
        private Provider provider;

        private String profileImg;
        private String username;
        private String nickname;

        private Gender gender;
        private SkinType skinType;
        private PersonalColor personalColor;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtistJoinDto {
        private String id_token;
        private Provider provider;

        private String profileImg;
        private String username;
        private String nickname;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtistExtraDto {
        private Long userId;
        private String profileImg;
        private String nickname;
        private Gender gender;
        private String introduction;
        private WorkExperience workExperience;
        private List<Region> region;
        private List<Category> specialization;
        private MakeupLocation makeupLocation;
        private String shopLocation;
        private Map<DayOfWeek, Times> availableDayOfWeekAndTime;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDto {
        private String id_token;
        private Provider provider;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReissueDto {
        private String accessToken;
        private String refreshToken;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessTokenDto {
        private String accessToken;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenDto {
        private String refreshToken;
    }
}
