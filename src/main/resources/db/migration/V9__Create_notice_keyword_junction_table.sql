CREATE TABLE notice_keyword (
    notice_id BIGINT NOT NULL,
    keyword_id BIGINT NOT NULL,
    PRIMARY KEY (notice_id, keyword_id),
    FOREIGN KEY (notice_id) REFERENCES NOTICE(id) ON DELETE CASCADE,
    FOREIGN KEY (keyword_id) REFERENCES KEYWORD(id) ON DELETE CASCADE
);

CREATE INDEX idx_notice_keyword_notice_id ON notice_keyword(notice_id);
CREATE INDEX idx_notice_keyword_keyword_id ON notice_keyword(keyword_id); 