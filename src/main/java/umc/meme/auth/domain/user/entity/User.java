package umc.meme.auth.domain.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import umc.meme.auth.domain.common.BaseTimeEntity;
import umc.meme.auth.global.enums.Gender;
import umc.meme.auth.global.enums.Provider;
import umc.meme.auth.global.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SuperBuilder @Getter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class User extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    protected String profileImg;

    @NotNull
    @Column(length = 40)
    protected String nickname;

    @NotNull
    @Column(unique = true, length = 100)
    private String username;

    @NotNull
    @Column(length = 40)
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String role;

    @NotNull
    private boolean details;  // 새롭게 추가, 기본 값은 false

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    protected Gender gender;

    @Column(nullable = true)
    private LocalDate inactiveDate;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @NotNull
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Provider provider;

    public boolean getDetails() {
        return details;
    }
}
