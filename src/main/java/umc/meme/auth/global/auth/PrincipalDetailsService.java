package umc.meme.auth.global.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import umc.meme.auth.domain.artist.domain.Artist;
import umc.meme.auth.domain.artist.domain.ArtistRepository;
import umc.meme.auth.domain.model.domain.Model;
import umc.meme.auth.domain.model.domain.ModelRepository;
import umc.meme.auth.domain.user.domain.User;
import umc.meme.auth.domain.user.domain.UserRepository;
import umc.meme.auth.global.common.status.ErrorStatus;
import umc.meme.auth.global.exception.handler.AritstHandler;
import umc.meme.auth.global.exception.handler.MemberHandler;
import umc.meme.auth.global.exception.handler.ModelHandler;

import java.util.Optional;

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
