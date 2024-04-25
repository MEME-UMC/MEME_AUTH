package umc.meme.auth.global.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import umc.meme.auth.annotation.IntegrationTest;
import umc.meme.auth.annotation.UnitTest;
import umc.meme.auth.domain.artist.entity.Artist;
import umc.meme.auth.domain.artist.entity.ArtistRepository;
import umc.meme.auth.domain.model.entity.Model;
import umc.meme.auth.domain.model.entity.ModelRepository;
import umc.meme.auth.domain.token.entity.TokenRepository;
import umc.meme.auth.domain.user.entity.User;
import umc.meme.auth.global.auth.dto.AuthRequest;
import umc.meme.auth.global.converter.UserConverter;
import umc.meme.auth.global.enums.Gender;
import umc.meme.auth.global.enums.PersonalColor;
import umc.meme.auth.global.enums.Provider;
import umc.meme.auth.global.enums.SkinType;
import umc.meme.auth.global.exception.AuthException;
import umc.meme.auth.global.jwt.JwtTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@IntegrationTest
class AuthServiceIntegrationTest {

}

@UnitTest
class AuthServiceUnitTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private ModelRepository modelRepository;
    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenRepository tokenRepository;

    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String TOKEN_PREFIX_WEIRD = "Bear ";

    @Test
    @DisplayName("모델 저장")
    void When_JoinModel_Expect_SaveModel() {
        // given
        String userName = "GEN Chovy";
        String userEmail = "test@naver.com";

        AuthRequest.ModelJoinDto modelJoinDto = createModelJoinDto(userName);
        Model model = UserConverter.toModel(modelJoinDto, userEmail, "MODEL");

        when(modelRepository.save(any(Model.class))).thenReturn(model);

        // when
        Model savedModel = authService.saveUser(modelJoinDto, userEmail);

        // then
        assertThat(savedModel.getUsername()).isEqualTo(userName);
        assertThat(savedModel.getEmail()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("아티스트 저장")
    void When_JoinArtist_Expect_SaveArtist() {
        // given
        String userName = "T1 Faker";
        String userEmail = "test@gmail.com";

        AuthRequest.ArtistJoinDto artistJoinDto = createArtistJoinDto(userName);
        Artist artist = UserConverter.toArtist(artistJoinDto, userEmail, "ARTIST");

        when(artistRepository.save(any(Artist.class))).thenReturn(artist);

        // when
        Artist savedArtist = authService.saveUser(artistJoinDto, userEmail);

        // then
        assertThat(savedArtist.getUsername()).isEqualTo(userName);
        assertThat(savedArtist.getEmail()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("로그인 성공 시 토큰 반환")
    void When_RequestUser_Expect_ReturnTokenPair() {
        // given
        String userName = "GEN Chovy";
        String userEmail = "test@naver.com";

        AuthRequest.ModelJoinDto modelJoinDto = createModelJoinDto(userName);
        User user = UserConverter.toModel(modelJoinDto, userEmail, "MODEL");

        String accessToken = "access_token";
        String refreshToken = "refresh_token";
        String[] tokenPair = new String[]{
                accessToken, refreshToken
        };

        // Authentication 객체까지 직접 우리가 구현해야하네
        Authentication authentication = new TestingAuthenticationToken(userName, userEmail);

        when(jwtTokenProvider.createTokenPair(any())).thenReturn(tokenPair);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);  // 일단 서비스에 있는 의존성은 다 넣어야 하나보다..

        // when
        String[] savedTokenPair = authService.login(user);

        // then
        assertThat(savedTokenPair[0]).isEqualTo(accessToken);
        assertThat(savedTokenPair[1]).isEqualTo(refreshToken);
    }

    @Test
    @DisplayName("올바른 Token Prefix")
    void When_RequestBearerTokenWithPrefix_Expect_TokenWithoutPrefix() {
        // given
        String bearerToken = TOKEN_PREFIX + "meme-test-token";

        // when
        String tokenWithoutPrefix = authService.resolveToken(bearerToken);

        // then
        assertThat(tokenWithoutPrefix).isEqualTo(bearerToken.substring(7));
    }

    @Test
    @DisplayName("올바르지 않은 Token Prefix")
    void When_RequestBearerTokenWithWeirdPrefix_Expect_TokenWithoutPrefix() {
        // given
        String bearerToken = TOKEN_PREFIX_WEIRD + "meme-test-token";

        // then
        assertThrows(AuthException.class, () -> authService.resolveToken(bearerToken));
    }

    private AuthRequest.ModelJoinDto createModelJoinDto(String userName) {
        return AuthRequest.ModelJoinDto.builder()
                .id_token("test_id_token")
                .provider(Provider.KAKAO)
                .profile_img("test_profile_img")
                .username(userName)
                .nickname("test_nickname")
                .gender(Gender.MALE)
                .skin_type(SkinType.COMMON)
                .personal_color(PersonalColor.SUMMER)
                .build();
    }
    private AuthRequest.ArtistJoinDto createArtistJoinDto(String userName) {
        return AuthRequest.ArtistJoinDto.builder()
                .id_token("test_id_token")
                .provider(Provider.KAKAO)
                .profile_img("test_profile_img")
                .username(userName)
                .nickname("test_nickname")
                .build();
    }
}