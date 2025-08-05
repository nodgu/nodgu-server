-- NotificationSetting 테이블 컬럼명 변경 및 인덱스 업데이트

-- 기존 인덱스 삭제
DROP INDEX IF EXISTS idx_notification_setting_keyword;

-- keyword 컬럼을 keyword_id로 변경
ALTER TABLE notification_setting RENAME COLUMN keyword TO keyword_id;

-- 새로운 인덱스 생성
CREATE INDEX idx_notification_setting_keyword_id ON notification_setting(keyword_id);

-- 기존 인덱스는 유지 (user_id 인덱스)

-- Notifications 테이블에서 keyword 컬럼 제거 (엔티티에서 주석 처리됨)
-- 기존 인덱스 삭제
DROP INDEX IF EXISTS idx_notifications_keyword;

-- keyword 컬럼 제거 (NOT NULL 제약조건이 있으므로 주의)
-- 먼저 NOT NULL 제약조건을 제거
ALTER TABLE notifications ALTER COLUMN keyword DROP NOT NULL;

-- keyword 컬럼 제거
ALTER TABLE notifications DROP COLUMN IF EXISTS keyword;

-- 외래키 제약조건 제거 (keyword 컬럼이 삭제되므로)
-- PostgreSQL에서는 컬럼 삭제 시 자동으로 외래키도 삭제됨 