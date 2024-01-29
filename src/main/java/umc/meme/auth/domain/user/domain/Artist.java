package umc.meme.auth.domain.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue(value = "Artist")
public class Artist extends User{
    @Column(nullable = true)
    private String gender;

    @Column(nullable = true, length = 40)
    private String nickname;

    @Column(nullable = true)
    private String profileImg;

    @Column(nullable = true, length = 500)
    private String introduction = "안녕하세요! 저는 ___입니다!";
}
