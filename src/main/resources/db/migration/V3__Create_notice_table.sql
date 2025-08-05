CREATE TABLE NOTICE (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    notitype VARCHAR(100) NOT NULL,
    description TEXT,
    notice_id VARCHAR(255) NOT NULL,
    date TIMESTAMP NOT NULL,
    tdindex TEXT,
    imgs JSONB,
    links JSONB,
    attachments JSONB,
    ocr_data TEXT,
    univ_code VARCHAR(100) NOT NULL,
    org_code VARCHAR(100) NOT NULL,
    sub_code VARCHAR(100),
    CONSTRAINT uk_notice_unique UNIQUE (univ_code, org_code, sub_code, notice_id)
);

CREATE INDEX idx_notice_date ON NOTICE(date);
CREATE INDEX idx_notice_univ_org ON NOTICE(univ_code, org_code);
CREATE INDEX idx_notice_notice_id ON NOTICE(notice_id);
