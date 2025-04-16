CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    order_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);