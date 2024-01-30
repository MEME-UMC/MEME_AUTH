package umc.meme.auth.domain.model.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import umc.meme.auth.domain.artist.domain.enums.Gender;
import umc.meme.auth.domain.model.domain.enums.PersonalColor;
import umc.meme.auth.domain.model.domain.enums.SkinType;
import umc.meme.auth.domain.user.domain.User;

import java.util.Date;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorValue(value = "Model")
@Entity
public class Model extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false, length = 40)
    private String nickname;

    @Column(nullable = false)
    private String profileImg;

    @Column(nullable = true, length = 500)
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkinType skinType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonalColor personalColor;

    private Date inactive;
}
