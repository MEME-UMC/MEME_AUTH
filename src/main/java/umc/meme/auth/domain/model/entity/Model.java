package umc.meme.auth.domain.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import umc.meme.auth.domain.model.entity.enums.PersonalColor;
import umc.meme.auth.domain.model.entity.enums.SkinType;
import umc.meme.auth.domain.user.entity.User;

@SuperBuilder @Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Model extends User {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkinType skinType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonalColor personalColor;
}
