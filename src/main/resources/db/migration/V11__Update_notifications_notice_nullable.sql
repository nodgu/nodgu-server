-- Notifications 테이블의 notice_id 컬럼을 null 가능하게 변경

-- 기존 NOT NULL 제약조건 제거
ALTER TABLE notifications ALTER COLUMN notice_id DROP NOT NULL;

-- 외래키 제약조건은 유지 (null 값도 허용)
