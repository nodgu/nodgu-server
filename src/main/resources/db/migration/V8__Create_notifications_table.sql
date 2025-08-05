CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    keyword BIGINT NOT NULL,
    title VARCHAR(255),
    description TEXT,
    remind_date TIMESTAMP,
    user_id BIGINT NOT NULL,
    notice_id BIGINT NOT NULL,
    notification_setting BIGINT,
    FOREIGN KEY (keyword) REFERENCES KEYWORD(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (notice_id) REFERENCES NOTICE(id) ON DELETE CASCADE,
    FOREIGN KEY (notification_setting) REFERENCES notification_setting(id) ON DELETE SET NULL
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_keyword ON notifications(keyword);
CREATE INDEX idx_notifications_notice_id ON notifications(notice_id);
CREATE INDEX idx_notifications_remind_date ON notifications(remind_date); 