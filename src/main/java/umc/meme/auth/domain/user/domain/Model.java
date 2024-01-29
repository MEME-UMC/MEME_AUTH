package umc.meme.auth.domain.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue(value = "Artist")
public class Model extends User{
    @Column(nullable = false)
    private String gender;

    @Column(nullable = false, length = 40)
    private String nickname;

    @Column(nullable = false)
    private String profileImg;

    @Column(nullable = true, length = 500)
    private String introduction;
}
