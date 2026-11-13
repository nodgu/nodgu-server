-- 비밀번호 재설정 시각. 이보다 먼저 발급된 JWT는 거부한다.
ALTER TABLE users ADD COLUMN password_changed_at TIMESTAMP;
