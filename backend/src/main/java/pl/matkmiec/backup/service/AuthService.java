package pl.matkmiec.backup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.matkmiec.backup.dto.AuthRequestDto;
import pl.matkmiec.backup.entity.User;
import pl.matkmiec.backup.exception.UserAlreadyExists;
import pl.matkmiec.backup.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Registers a new user. */
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

    @Transactional
    public String login(AuthRequestDto authRequestDto) {
        return "token";
    }

}
