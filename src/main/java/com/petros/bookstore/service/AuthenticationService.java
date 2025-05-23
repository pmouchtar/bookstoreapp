package com.petros.bookstore.service;

import static com.petros.bookstore.model.enums.Role.USER;

import com.petros.bookstore.dto.AuthenticationRequestDto;
import com.petros.bookstore.dto.AuthenticationResponseDto;
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

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  private final UserRepository userRepository;
  private final ShoppingCartRepository shoppingCartRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationResponseDto authenticate(final AuthenticationRequestDto request) {

    final var authToken =
        UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password());

    // final var authentication =
    authenticationManager.authenticate(authToken);

    final var token = jwtService.generateToken(request.username());
    return new AuthenticationResponseDto(token);
  }

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

    // when a user registers, a new unique shopping cart is attached to him
    Shopping_Cart shoppingCart = new Shopping_Cart();
    shoppingCart.setUser(savedUser);
    shoppingCartRepository.save(shoppingCart);

    return savedUser;
  }
}
