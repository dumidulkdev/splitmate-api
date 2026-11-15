package com.splitmate.settlement;

import com.splitmate.common.StandardErrors;
import com.splitmate.settlement.dto.RecordSettlementRequest;
import com.splitmate.settlement.dto.SettlementResponse;
import com.splitmate.settlement.dto.SettlementSuggestion;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/groups/{groupId}")
@Tag(name = "Settlements", description = "Suggest the shortest list of payments, and record the ones that actually happen")
@StandardErrors
public class SettlementController {

    private final SettleUpService settleUp;
    private final SettlementService settlements;

    public SettlementController(SettleUpService settleUp, SettlementService settlements) {
        this.settleUp = settleUp;
        this.settlements = settlements;
    }

    @GetMapping("/settle-up")
    @Operation(
            summary = "Suggest settle-up transfers",
            description = """
                    Matches the largest creditor with the largest debtor until nobody is owed money. \
                    The result is a short list of payments, not every pairwise debt. An empty list \
                    means the group is already settled. Recording a settlement is a separate step."""
    )
    @ApiResponse(responseCode = "200", description = "Payments that would settle the group",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = SettlementSuggestion.class)),
                    examples = @ExampleObject(name = "Carla pays Ana, then Bruno", value = """
                            [
                              {
                                "fromMemberId": "7c2f1b4f-2d3e-5f60-9b0c-1d2e3f4a5b6c",
                                "fromMemberName": "Carla",
                                "toMemberId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                "toMemberName": "Ana",
                                "amount": 40.00
                              },
                              {
                                "fromMemberId": "7c2f1b4f-2d3e-5f60-9b0c-1d2e3f4a5b6c",
                                "fromMemberName": "Carla",
                                "toMemberId": "6b1e0a3e-1c2d-4e5f-8a9b-0c1d2e3f4a5b",
                                "toMemberName": "Bruno",
                                "amount": 10.00
                              }
                            ]
                            """)))
    public List<SettlementSuggestion> suggest(
            @Parameter(description = "Group id", example = "2d9b0c1a-8e7f-4c6d-9b5a-1f0e2d3c4b5a")
            @PathVariable UUID groupId) {
        return settleUp.suggest(groupId);
    }

    @PostMapping("/settlements")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Record a settlement",
            description = """
                    Records money that actually moved from one member to another. \
                    The payer's balance goes up and the receiver's balance goes down by the same amount. \
                    The payment does not have to match a suggestion."""
    )
    @ApiResponse(responseCode = "201", description = "Settlement recorded",
            content = @Content(schema = @Schema(implementation = SettlementResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = @ExampleObject(
            name = "Carla pays Ana",
            value = """
                    {
                      "fromMemberId": "7c2f1b4f-2d3e-5f60-9b0c-1d2e3f4a5b6c",
                      "toMemberId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                      "amount": 40.00,
                      "note": "Dinner and tickets"
                    }
                    """)))
    public SettlementResponse record(@PathVariable UUID groupId, @Valid @RequestBody RecordSettlementRequest request) {
        return settlements.record(groupId, request);
    }

    @GetMapping("/settlements")
    @Operation(summary = "List settlements", description = "Payments recorded in this group, oldest first.")
    @ApiResponse(responseCode = "200", description = "Recorded payments")
    public List<SettlementResponse> list(@Parameter(description = "Group id") @PathVariable UUID groupId) {
        return settlements.list(groupId);
    }
}
