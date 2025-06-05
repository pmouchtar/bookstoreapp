package com.petros.bookstore.config;

import com.petros.bookstore.repository.FavouriteBookRepository;
import com.petros.bookstore.service.*;
import com.petros.bookstore.utils.AuthUtils;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestServiceConfig {

    @Bean
    public AuthenticationService authenticationService() {
        return Mockito.mock(AuthenticationService.class);
    }

    @Bean
    public AuthUtils authUtils() {
        return Mockito.mock(AuthUtils.class);
    }

    @Bean
    public FavouriteBookService favouriteBookService() {
        return Mockito.mock(FavouriteBookService.class);
    }

    @Bean
    public FavouriteBookRepository favouriteBookRepository() {
        return Mockito.mock(FavouriteBookRepository.class);
    }

    @Bean
    public ShoppingCartService shoppingCartService() {
        return Mockito.mock(ShoppingCartService.class);
    }

    @Bean
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    public OrderService orderService() {
        return Mockito.mock(OrderService.class);
    }
}