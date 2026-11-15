package com.splitmate.group;

import com.splitmate.common.StandardErrors;
import com.splitmate.group.dto.AddMemberRequest;
import com.splitmate.group.dto.CreateGroupRequest;
import com.splitmate.group.dto.GroupResponse;
import com.splitmate.group.dto.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Groups", description = "Create a group and manage who is in it")
@StandardErrors
public class GroupController {

    private final GroupService groups;

    public GroupController(GroupService groups) {
        this.groups = groups;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a group", description = "Starts an empty group. Members are added separately.")
    @ApiResponse(responseCode = "201", description = "Group created",
            content = @Content(schema = @Schema(implementation = GroupResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(
            name = "Weekend trip",
            value = "{\"name\":\"Weekend in Porto\"}")))
    public GroupResponse create(@Valid @RequestBody CreateGroupRequest request) {
        return groups.create(request);
    }

    @GetMapping
    @Operation(summary = "List groups", description = "Newest groups first, each with its members.")
    @ApiResponse(responseCode = "200", description = "Groups currently stored")
    public List<GroupResponse> list() {
        return groups.list();
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "Get a group", description = "Returns the group and every member, sorted by name.")
    @ApiResponse(responseCode = "200", description = "The group",
            content = @Content(schema = @Schema(implementation = GroupResponse.class)))
    public GroupResponse get(@Parameter(description = "Group id", example = "2d9b0c1a-8e7f-4c6d-9b5a-1f0e2d3c4b5a")
                             @PathVariable UUID groupId) {
        return groups.get(groupId);
    }

    @PostMapping("/{groupId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a member",
            description = "Names must be unique in the group. Comparison ignores case, so 'Ana' and 'ana' clash.")
    @ApiResponse(responseCode = "201", description = "Member added",
            content = @Content(schema = @Schema(implementation = MemberResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(
            name = "Ana",
            value = "{\"name\":\"Ana\"}")))
    public MemberResponse addMember(@PathVariable UUID groupId, @Valid @RequestBody AddMemberRequest request) {
        return groups.addMember(groupId, request);
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a member",
            description = "Removes someone who has no expenses or settlements in the group.")
    @ApiResponse(responseCode = "204", description = "Member removed")
    public void removeMember(@PathVariable UUID groupId, @PathVariable UUID memberId) {
        groups.removeMember(groupId, memberId);
    }
}
