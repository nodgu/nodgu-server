package io.github.nodgu.core_server.domain.notice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.nodgu.core_server.domain.notice.entity.Notice;
import io.github.nodgu.core_server.domain.notice.repository.NoticeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class NoticeListImgsIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired NoticeRepository noticeRepository;

    @Test
    @DisplayName("공지 목록 응답의 각 공지에 imgs가 문자열 배열로 들어가고, 이미지가 없으면 빈 배열이다")
    void noticeListIncludesImgs() throws Exception {
        String notitype = "imgs-test-" + UUID.randomUUID().toString().substring(0, 8);
        List<String> imgs = List.of("https://example.com/a.png", "https://example.com/b.jpg");

        Notice withImgs = notice(notitype, LocalDateTime.now().plusMinutes(2), imgs);
        Notice withoutImgs = notice(notitype, LocalDateTime.now().plusMinutes(1), null);

        MvcResult result = mockMvc.perform(get("/api/v1/notice/notice").param("notitype", notitype)).andReturn();
        assertEquals(200, result.getResponse().getStatus());

        JsonNode notices = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("notices");
        assertEquals(2, notices.size());

        JsonNode first = notices.get(0);
        assertEquals(withImgs.getId().longValue(), first.path("id").asLong());
        assertTrue(first.path("imgs").isArray());
        assertEquals(imgs, objectMapper.convertValue(first.path("imgs"), List.class));

        JsonNode second = notices.get(1);
        assertEquals(withoutImgs.getId().longValue(), second.path("id").asLong());
        assertTrue(second.path("imgs").isArray());
        assertEquals(0, second.path("imgs").size());
    }

    private Notice notice(String notitype, LocalDateTime date, List<String> imgs) {
        Notice n = new Notice();
        n.setTitle("공지");
        n.setUrl("https://example.com/n");
        n.setNotitype(notitype);
        n.setNoticeId("n-" + UUID.randomUUID());
        n.setDate(date);
        n.setImgs(imgs);
        n.setUnivCode("u");
        n.setOrgCode("o");
        return noticeRepository.save(n);
    }
}
