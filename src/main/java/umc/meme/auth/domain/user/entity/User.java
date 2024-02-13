package umc.meme.auth.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import umc.meme.auth.domain.artist.entity.enums.Gender;
import umc.meme.auth.global.enums.Provider;
import umc.meme.auth.global.enums.UserStatus;

import java.time.LocalDate;

@SuperBuilder @Getter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    protected String profileImg;

    @NotNull
    @Column(length = 40)
    protected String nickname;

    @NotNull
    @Column(unique = true, length = 20)
    private String username;

    @NotNull
    @Column(length = 40)
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    protected Gender gender;

    @Column(nullable = true)
    private LocalDate inactiveDate;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Provider provider;
}
