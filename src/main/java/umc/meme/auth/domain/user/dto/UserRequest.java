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
    public static class JoinDto {
        private String username;
        private String email;
        private String role;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class modelJoinDto {
        private String username;
        private String email;
        private String role;
        private String profileSrc;
        private String nickname;
        private String gender;
        private String skinType;
        private String personalColor;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class artistJoinDto {
        private String username;
        private String email;
        private String role;
        private String profileSrc;
        private String nickname;
    }

    @Data @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class artistExtraDto {
        private Long artistId;
        private String nickname;
        private String gender;
        private String profileSrc;
        private String introduction;
        private String workExperience;
        private String makeupLocation;
        private String availableTime;
        private List<String> region;

//        private List<Category> specialization;
    }
}
