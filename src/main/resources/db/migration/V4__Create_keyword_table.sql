CREATE TABLE KEYWORD (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT uk_keyword_user_title UNIQUE (user_id, title),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_keyword_user_id ON KEYWORD(user_id);
CREATE INDEX idx_keyword_title ON KEYWORD(title); 