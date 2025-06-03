package com.petros.bookstore.service;

import static com.petros.bookstore.model.enums.Role.USER;

import com.petros.bookstore.dto.AuthDTO.AuthenticationRequestDto;
import com.petros.bookstore.dto.AuthDTO.AuthenticationResponseDto;
import com.petros.bookstore.model.Shopping_Cart;
import com.petros.bookstore.model.User;
import com.petros.bookstore.repository.ShoppingCartRepository;
import com.petros.bookstore.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling user authentication and registration
 * logic.
 * <p>
 * It integrates with Spring Security to authenticate users using JWT and
 * manages the creation of new users along with their shopping carts.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private final UserRepository userRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticates a user using the provided credentials and returns a JWT token.
     *
     * @param request
     *            The authentication request DTO containing the username and
     *            password.
     * @return {@link AuthenticationResponseDto} containing a generated JWT token.
     * @throws org.springframework.security.core.AuthenticationException
     *             if the credentials are invalid.
     */
    public AuthenticationResponseDto authenticate(final AuthenticationRequestDto request) {
        final var authToken = UsernamePasswordAuthenticationToken.unauthenticated(request.username(),
                request.password());

        authenticationManager.authenticate(authToken); // will throw if invalid

        final var token = jwtService.generateToken(request.username());
        return new AuthenticationResponseDto(token);
    }

    /**
     * Registers a new user with role USER and creates an associated shopping cart.
     * <p>
     * This method also encodes the password and checks for username uniqueness.
     *
     * @param request
     *            The user entity containing the registration information.
     * @return The saved {@link User} entity.
     * @throws ValidationException
     *             if the username already exists.
     */
    @Transactional
    public User registerUser(User request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ValidationException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        // Create and link a shopping cart to the newly registered user
        Shopping_Cart shoppingCart = new Shopping_Cart();
        shoppingCart.setUser(savedUser);
        shoppingCartRepository.save(shoppingCart);

        return savedUser;
    }
}
