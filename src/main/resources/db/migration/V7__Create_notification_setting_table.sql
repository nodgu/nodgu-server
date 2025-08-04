CREATE TABLE notification_setting (
    id BIGSERIAL PRIMARY KEY,
    keyword BIGINT NOT NULL,
    title VARCHAR(255),
    alarm_days INTEGER,
    alarm_time VARCHAR(10),
    user_id BIGINT NOT NULL,
    FOREIGN KEY (keyword) REFERENCES KEYWORD(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_notification_setting_user_id ON notification_setting(user_id);
CREATE INDEX idx_notification_setting_keyword ON notification_setting(keyword); 