package umc.meme.auth.global.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import umc.meme.auth.domain.model.entity.ModelRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private ModelRepository modelRepository;
    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("모델 회원가입 - 정상")
    void When_ModelNicknameUnique_Expect_ReturnTokenDto() {
        // given

        // when

        // then

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