package umc.meme.auth.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import umc.meme.auth.domain.artist.entity.ArtistRepository;
import umc.meme.auth.domain.model.entity.ModelRepository;
import umc.meme.auth.domain.user.domain.User;
import umc.meme.auth.domain.user.domain.UserRepository;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.handler.MemberHandler;

@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final ModelRepository modelRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        return new PrincipalDetails(user);
    }
}
