package com.petros.bookstore.config;

import com.petros.bookstore.service.FavouriteBookService;
import com.petros.bookstore.service.OrderService;
import com.petros.bookstore.service.ShoppingCartService;
import com.petros.bookstore.service.UserService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestServiceConfig {

    @Bean
    public FavouriteBookService favouriteBookService() {
        return Mockito.mock(FavouriteBookService.class);
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