package umc.meme.auth.global.oauth.jwk;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@NoArgsConstructor
@Table(name = "jwk")
public class JWK {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String kid;
    private String kty;
    private String alg;
    private String u;
    @Column(length = 342)
    private String n;
    private String e;
}
