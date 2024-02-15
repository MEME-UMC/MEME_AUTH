package umc.meme.auth.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import umc.meme.auth.global.enums.PersonalColor;
import umc.meme.auth.global.enums.SkinType;
import umc.meme.auth.global.enums.*;

import java.util.List;
import java.util.Map;

public class UserRequest {
    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelJoinDto {
        // OAuth
        private String email;
        private Provider provider;

        // User
        private String profileImg;
        private String username;
        private String nickname;

        // Model
        private Gender gender;
        private SkinType skinType;
        private PersonalColor personalColor;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArtistJoinDto {
        // OAuth
        private String email;
        private Provider provider;

        // User
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
}
