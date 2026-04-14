package pl.matkmiec.backup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.matkmiec.backup.dto.AuthRequestDto;
import pl.matkmiec.backup.entity.User;
import pl.matkmiec.backup.exception.PasswordMismatchException;
import pl.matkmiec.backup.exception.UserAlreadyExists;
import pl.matkmiec.backup.exception.UserNotFoundException;
import pl.matkmiec.backup.repository.UserRepository;
import pl.matkmiec.backup.security.JwtService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    /** The repository used for user data access. */
    private final UserRepository userRepository;

    /** The password encoder used for hashing passwords. */
    private final PasswordEncoder passwordEncoder;

    /** The JWT service used for generating and verifying JWT tokens. */
    private final JwtService jwtService;

    /** Register a new user.
     * @param authRequestDto The request body containing user information.
     * @throws UserAlreadyExists If the user already exists.
     */
    @Transactional
    public void register(AuthRequestDto authRequestDto) throws UserAlreadyExists {

        /** Check if user already exists */
        if(userRepository.existsByUsername(authRequestDto.getUsername())){
            throw new UserAlreadyExists();
        }

        /** Create user */
        User user = User.builder()
                .username(authRequestDto.getUsername())
                .password(passwordEncoder.encode(authRequestDto.getPassword()))
                .createdAt(LocalDateTime.now())
                .build();

        /** Save user */
        userRepository.save(user);
    }

    /** Login a user.
     *
     * @param authRequestDto The request body containing user credentials.
     * @return A JWT token representing the user's authentication.
     * @throws UserNotFoundException If the user is not found.*/
    @Transactional
    public String login(AuthRequestDto authRequestDto) throws UserNotFoundException, PasswordMismatchException {
        User user = userRepository.findByUsername(authRequestDto.getUsername())
                .orElseThrow(UserNotFoundException::new);

        if(!passwordEncoder.matches(authRequestDto.getPassword(), user.getPassword())){
            throw new PasswordMismatchException();
        }

        return jwtService.generateToken(user.getUsername());
    }

}
