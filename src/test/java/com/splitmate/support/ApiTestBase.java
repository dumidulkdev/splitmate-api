package com.splitmate.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public abstract class ApiTestBase {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper json;

    protected JsonNode postJson(String path, Object body) throws Exception {
        MvcResult result = mvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }

    protected JsonNode getJson(String path) throws Exception {
        MvcResult result = mvc.perform(get(path))
                .andExpect(status().isOk())
                .andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }

    protected UUID createGroup(String name) throws Exception {
        return UUID.fromString(postJson("/api/groups", Map.of("name", name)).get("id").asText());
    }

    protected UUID addMember(UUID groupId, String name) throws Exception {
        return UUID.fromString(postJson("/api/groups/" + groupId + "/members", Map.of("name", name)).get("id").asText());
    }
}
