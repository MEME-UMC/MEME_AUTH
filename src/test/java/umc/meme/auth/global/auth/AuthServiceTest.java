package umc.meme.auth.global.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import umc.meme.auth.domain.model.entity.ModelRepository;
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.global.auth.dto.AuthResponse;
import umc.meme.auth.global.config.EmbeddedRedisConfig;
import umc.meme.auth.global.config.RedisConfig;
import umc.meme.auth.global.enums.Gender;
import umc.meme.auth.global.enums.PersonalColor;
import umc.meme.auth.global.enums.Provider;
import umc.meme.auth.global.enums.SkinType;
import umc.meme.auth.global.infra.RedisRepository;
import umc.meme.auth.global.oauth.service.OAuthService;
import umc.meme.auth.global.oauth.service.kakao.KakaoAuthService;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private ModelRepository modelRepository;
    @Mock
    private RedisRepository redisRepository;
    @Mock
    private KakaoAuthService kakaoAuthService;
    @Mock
    private OAuthService oAuthService;
    @Mock
    private EmbeddedRedisConfig embeddedRedisConfig;
    @Mock
    private RedisConfig redisConfig;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("모델 회원가입 - 정상")
    void When_ModelNicknameUnique_Expect_ReturnTokenDto() throws IOException {
        // given
        Long userId = 1L;
        AuthRequest.ModelJoinDto modelJoinDto = AuthRequest.ModelJoinDto.builder()
                .id_token("eyJraWQiOiI5ZjI1MmRhZGQ1ZjIzM2Y5M2QyZmE1MjhkMTJmZWEiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIzZDliYTI3ZTc5ZTY0Y2Y4NTdlOGU5MWJiZmM4ODM0NiIsInN1YiI6IjMzMjE5NDYxNTYiLCJhdXRoX3RpbWUiOjE3MTMxNDczODMsImlzcyI6Imh0dHBzOi8va2F1dGgua2FrYW8uY29tIiwibmlja25hbWUiOiLsnoTsnqzsmIEiLCJleHAiOjE3MTMxNjg5ODMsImlhdCI6MTcxMzE0NzM4MywiZW1haWwiOiJsaW1qeWp1c3RpbkBuYXZlci5jb20ifQ.M2gnAzq4kpCwVtGbbSIc-gArKLLlu3MXjahKK-zO-TxhkcLbl_2PC061Dapde1qJjuEKIRoTjzSWWrOk9GFgPeBu_01bFysujJOJdrwXmHd8jJtINFfBP_JDIkXDh4tqIc6BxqBHsq6VD8V1hebf9k7547XTZ3Fh8boNpA-vNHyvhUJwZW1BKUx4vNrK8nxSku3eCAJbOEidVYCKT_zEbAgQwWhSiskDO7E2mGnNeM0hYygGvDt8LHPFwPGcKaTs9YXcoV5X2AleLauSZLO70nJ3fAX6DkCOSSBU_dy2W8zjqP-nNXAMAh-MgbEonl4U-uM-fkjQmlTsM2mKErTUUA")
                .provider(Provider.KAKAO)
                .profile_img("profileImgLink")
                .username("임재영")
                .nickname("제이스")
                .gender(Gender.MALE)
                .skin_type(SkinType.DRY)
                .personal_color(PersonalColor.AUTUMN)
                .build();

        // 이게 어느 동작을 정의해주는지 모르겠네
        // 서비스 안에서 굴러가는 Repository 메서드에 대한건지
        // 아니면 이 메서드 안에 있는 것들에 대한 동작을 정의해주는건지
        // Mockito.when(new Model().getUserId()).thenReturn(userId);

        // 뭔가 새로운 방법으로 테스트 방식을 고안해야 할 것 같아
        // 일단 Redis 안에 데이터가 저장되는지부터 봐야할듯
        // 그리고 Redis 사용하지 않아도 테스트 할 수 있는 방법 생각해보기
        // 이렇게 JWT 토큰 있는 것들은 단위 테스트 어떻게 하는거지 도대체?

        // when
        AuthResponse.TokenDto tokenDto = authService.signupModel(modelJoinDto);

        // then
        System.out.println("tokenDto.getAccessToken() = " + tokenDto.getAccessToken());

    }

    @Test
    @DisplayName("모델 회원가입 - 닉네임 중복 예외")
    void When_ModelNicknameExists_Expect_NicknameDuplicatedException() {

    }

    @Test
    @DisplayName("아티스트 회원가입 - 정상")
    void When_ArtistNicknameUnique_Expect_ReturnTokenDto() {

    }

    @Test
    @DisplayName("아티스트 회원가입 - 닉네임 중복 예외")
    void When_ArtistNicknameExists_Expect_NicknameDuplicatedException() {

    }
}