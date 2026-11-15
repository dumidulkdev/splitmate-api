package com.splitmate.balance;

import com.splitmate.balance.dto.BalanceResponse;
import com.splitmate.common.StandardErrors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups/{groupId}/balances")
@Tag(name = "Balances", description = "Who is owed, and who owes, after expenses and settlements")
@StandardErrors
public class BalanceController {

    private final BalanceService balances;

    public BalanceController(BalanceService balances) {
        this.balances = balances;
    }

    @GetMapping
    @Operation(
            summary = "Show net balances",
            description = """
                    Each member's balance is what they paid minus their shares of expenses, \
                    adjusted by any settlements. A positive balance means the group owes this \
                    member. The balances of a group always sum to zero."""
    )
    @ApiResponse(responseCode = "200", description = "One row per current member, sorted by name")
    public List<BalanceResponse> list(@Parameter(description = "Group id", example = "2d9b0c1a-8e7f-4c6d-9b5a-1f0e2d3c4b5a")
                                      @PathVariable UUID groupId) {
        return balances.forGroup(groupId);
    }
}
