package umc.meme.auth.domain.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import umc.meme.auth.domain.user.domain.User;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value = "Model")
@Entity
public class Model extends User {

    @Column(nullable = true)
    private String gender;

    @Column(nullable = true, length = 40)
    private String nickname;

    @Column(nullable = true)
    private String profileImg;

    @Column(nullable = true, length = 500)
    private String introduction = "안녕하세요! 저는 ___입니다!";
}
