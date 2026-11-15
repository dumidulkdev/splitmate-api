package com.splitmate.group;

import com.splitmate.support.ApiTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GroupApiTest extends ApiTestBase {

    @Test
    void createsListsAndFetchesAGroup() throws Exception {
        UUID groupId = createGroup("Weekend in Porto");

        mvc.perform(get("/api/groups/" + groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Weekend in Porto"))
                .andExpect(jsonPath("$.members", hasSize(0)))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        mvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(groupId.toString()));
    }

    @Test
    void addsAndRemovesMembers() throws Exception {
        UUID groupId = createGroup("House");
        UUID ana = addMember(groupId, "Ana");
        addMember(groupId, "Bruno");

        mvc.perform(get("/api/groups/" + groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members[*].name", containsInAnyOrder("Ana", "Bruno")));

        mvc.perform(delete("/api/groups/" + groupId + "/members/" + ana))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/groups/" + groupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members", hasSize(1)))
                .andExpect(jsonPath("$.members[0].name").value("Bruno"));
    }

    @Test
    void rejectsADuplicateNameIgnoringCase() throws Exception {
        UUID groupId = createGroup("House");
        addMember(groupId, "Ana");

        mvc.perform(post("/api/groups/" + groupId + "/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" ana \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("A member named 'ana' is already in this group"))
                .andExpect(jsonPath("$.path").value("/api/groups/" + groupId + "/members"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(0)));
    }

    @Test
    void validationErrorUsesTheSharedShape() throws Exception {
        mvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/groups"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("name"))
                .andExpect(jsonPath("$.fieldErrors[0].message").value("is required"));
    }

    @Test
    void missingGroupIsANotFoundError() throws Exception {
        UUID missing = UUID.fromString("00000000-0000-0000-0000-000000000099");

        mvc.perform(get("/api/groups/" + missing))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Group " + missing + " was not found"))
                .andExpect(jsonPath("$.path").value("/api/groups/" + missing))
                .andExpect(jsonPath("$.fieldErrors", hasSize(0)));
    }
}
