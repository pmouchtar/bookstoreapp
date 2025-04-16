ALTER TABLE order_items
ADD CONSTRAINT unique_book_order UNIQUE (book_id, order_id);

ALTER TABLE cart_items
ADD CONSTRAINT unique_book_cart UNIQUE (book_id, shopping_cart_id);
