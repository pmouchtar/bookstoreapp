CREATE TABLE cart_items (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    shopping_cart_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (shopping_cart_id) REFERENCES shopping_cart(id) ON DELETE CASCADE
);
