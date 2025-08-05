CREATE TABLE scrap (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notice_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (notice_id) REFERENCES NOTICE(id) ON DELETE CASCADE,
    CONSTRAINT uk_scrap_user_notice UNIQUE (user_id, notice_id)
);

CREATE INDEX idx_scrap_user_id ON scrap(user_id);
CREATE INDEX idx_scrap_notice_id ON scrap(notice_id); 