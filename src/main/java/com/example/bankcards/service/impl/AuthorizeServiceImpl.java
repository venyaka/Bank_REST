package com.example.bankcards.service.impl;

import com.example.bankcards.exception.AuthorizeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.bankcards.dto.request.RegisterReqDTO;
import com.example.bankcards.dto.request.UserAuthorizeReqDTO;
import com.example.bankcards.dto.response.TokenRespDTO;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.BadRequestException;
import com.example.bankcards.exception.NotFoundException;
import com.example.bankcards.exception.errors.AuthorizedError;
import com.example.bankcards.exception.errors.BadRequestError;
import com.example.bankcards.exception.errors.NotFoundError;
import com.example.bankcards.security.jwt.JwtUtils;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.AuthorizeService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthorizeServiceImpl implements AuthorizeService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final MailServiceImpl mailService;

    private final SessionServiceImpl sessionService;

    private final JwtUtils jwtUtils;

    private final CookieServiceImpl cookieService;

    @Override
    public ResponseEntity<TokenRespDTO> authorizeUser(UserAuthorizeReqDTO userAuthorizeDTO, HttpServletResponse response) {
        String userEmail = userAuthorizeDTO.getEmail();
        String userPassword = userAuthorizeDTO.getPassword();
        Optional<User> userOptional = userRepository.findByEmail(userEmail);

        if (userOptional.isEmpty()) {
            throw new AuthorizeException(AuthorizedError.USER_WITH_THIS_EMAIL_NOT_FOUND);
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(userPassword, user.getPassword())) {
            throw new AuthorizeException(AuthorizedError.NOT_CORRECT_PASSWORD);
        }
        checkUserCanAuthorize(user);
        user.setRefreshToken(jwtUtils.generateRandomSequence());
        String jwtToken = jwtUtils.generateToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);
        userRepository.saveAndFlush(user);

        TokenRespDTO tokenDTO = new TokenRespDTO();
        tokenDTO.setAccessToken("Bearer " + jwtToken);
        tokenDTO.setRefreshToken("Bearer " + refreshToken);

        sessionService.saveNewSession(user.getId());

        cookieService.addAuthCookies(response, jwtToken, refreshToken);

        return ResponseEntity.ok(tokenDTO);
    }

    @Override
    @Transactional
    public void registerUser(@Valid RegisterReqDTO registerDTO, HttpServletRequest request) {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new BadRequestException(BadRequestError.USER_ALREADY_EXISTS);
        }
        User user = new User();
        user.setEmail(registerDTO.getEmail());
        user.setFirstName(registerDTO.getFirstName());
        user.setLastName(registerDTO.getLastName());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setIsEmailVerificated(Boolean.FALSE);
        user.setToken(generateValidatingToken());
        userRepository.save(user);

        mailService.sendUserVerificationMail(user, request);

    }

    @Override
    public void sendVerificationCode(String email, HttpServletRequest request) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(NotFoundError.USER_NOT_FOUND));
        if (user.getIsEmailVerificated()) {
            throw new BadRequestException(BadRequestError.USER_ALREADY_VERIFICATED);
        }
        mailService.sendUserVerificationMail(user, request);

    }

    @Override
    @Transactional
    public void verificateUser(String email, String verificationToken) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(NotFoundError.USER_NOT_FOUND);
        }
        User user = optionalUser.get();

        if (user.getIsEmailVerificated()) {
            throw new BadRequestException(BadRequestError.USER_ALREADY_VERIFICATED);
        }

        if (null == user.getToken() || !user.getToken().equals(verificationToken)) {
            throw new BadRequestException(BadRequestError.NOT_CORRECT_VERIFICATION_CODE);
        }

        user.setToken(null);
        user.setIsEmailVerificated(Boolean.TRUE);
        userRepository.save(user);

    }


    private void checkUserCanAuthorize(User user) {
        if (!user.getIsEmailVerificated()) {
            throw new AuthorizeException(AuthorizedError.USER_NOT_VERIFY);
        }
    }

    private String generateValidatingToken() {
        return RandomStringUtils.randomAlphanumeric(50);
    }
}
