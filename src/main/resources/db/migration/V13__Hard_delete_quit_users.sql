-- 회원탈퇴를 실제 삭제로 바꾸면서 정리하는 마이그레이션
-- 1) password_reset_token FK에도 ON DELETE CASCADE를 걸어 다른 사용자 관련 테이블과 맞춘다.
ALTER TABLE password_reset_token DROP CONSTRAINT fk_password_reset_token_user;
ALTER TABLE password_reset_token
    ADD CONSTRAINT fk_password_reset_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- 2) 지금까지 탈퇴 처리로 is_active = FALSE가 된 회원과 관련 데이터를 실제로 삭제한다.
--    (이 시점까지 is_active는 탈퇴 용도로만 쓰였다.)
--    CASCADE에만 기대지 않고 자식 테이블부터 지운다.
DELETE FROM notifications
    WHERE user_id IN (SELECT id FROM users WHERE is_active = FALSE);
DELETE FROM notification_setting
    WHERE user_id IN (SELECT id FROM users WHERE is_active = FALSE);
DELETE FROM notice_keyword
    WHERE keyword_id IN (SELECT k.id FROM keyword k JOIN users u ON k.user_id = u.id WHERE u.is_active = FALSE);
DELETE FROM keyword
    WHERE user_id IN (SELECT id FROM users WHERE is_active = FALSE);
DELETE FROM scrap
    WHERE user_id IN (SELECT id FROM users WHERE is_active = FALSE);
DELETE FROM search_history
    WHERE user_id IN (SELECT id FROM users WHERE is_active = FALSE);
DELETE FROM devices
    WHERE user_id IN (SELECT id FROM users WHERE is_active = FALSE);
DELETE FROM password_reset_token
    WHERE user_id IN (SELECT id FROM users WHERE is_active = FALSE);
DELETE FROM users WHERE is_active = FALSE;
