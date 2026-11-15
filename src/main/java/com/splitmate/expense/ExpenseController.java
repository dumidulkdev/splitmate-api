package com.splitmate.expense;

import com.splitmate.common.StandardErrors;
import com.splitmate.expense.dto.CreateExpenseRequest;
import com.splitmate.expense.dto.ExpenseResponse;
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
@RequestMapping("/api/groups/{groupId}/expenses")
@Tag(name = "Expenses", description = "Record what was paid and how it is split")
@StandardErrors
public class ExpenseController {

    private final ExpenseService expenses;

    public ExpenseController(ExpenseService expenses) {
        this.expenses = expenses;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Add an expense",
            description = """
                    One member pays the total. EQUAL ignores value and splits the cents evenly, \
                    giving any leftover cent to the lowest member id. EXACT uses value as an amount \
                    and the amounts must sum to the total. PERCENTAGE uses value as a percent and \
                    the percentages must sum to 100. The payer does not have to be one of the participants."""
    )
    @ApiResponse(responseCode = "201", description = "Expense recorded, with each share rounded to cents",
            content = @Content(schema = @Schema(implementation = ExpenseResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(examples = {
            @ExampleObject(name = "Equal", description = "Three people share 90.00", value = """
                    {
                      "description": "Dinner at Cervejaria",
                      "amount": 90.00,
                      "paidBy": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                      "splitType": "EQUAL",
                      "shares": [
                        {"memberId": "3fa85f64-5717-4562-b3fc-2c963f66afa6"},
                        {"memberId": "6b1e0a3e-1c2d-4e5f-8a9b-0c1d2e3f4a5b"},
                        {"memberId": "7c2f1b4f-2d3e-5f60-9b0c-1d2e3f4a5b6c"}
                      ]
                    }
                    """),
            @ExampleObject(name = "Exact", description = "Amounts must sum to the total", value = """
                    {
                      "description": "Groceries",
                      "amount": 42.50,
                      "paidBy": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                      "splitType": "EXACT",
                      "shares": [
                        {"memberId": "3fa85f64-5717-4562-b3fc-2c963f66afa6", "value": 12.50},
                        {"memberId": "6b1e0a3e-1c2d-4e5f-8a9b-0c1d2e3f4a5b", "value": 30.00}
                      ]
                    }
                    """),
            @ExampleObject(name = "Percentage", description = "Percentages must sum to 100", value = """
                    {
                      "description": "Tickets",
                      "amount": 40.00,
                      "paidBy": "6b1e0a3e-1c2d-4e5f-8a9b-0c1d2e3f4a5b",
                      "splitType": "PERCENTAGE",
                      "shares": [
                        {"memberId": "3fa85f64-5717-4562-b3fc-2c963f66afa6", "value": 50.00},
                        {"memberId": "7c2f1b4f-2d3e-5f60-9b0c-1d2e3f4a5b6c", "value": 50.00}
                      ]
                    }
                    """)
    }))
    public ExpenseResponse add(@PathVariable UUID groupId, @Valid @RequestBody CreateExpenseRequest request) {
        return expenses.add(groupId, request);
    }

    @GetMapping
    @Operation(summary = "List expenses", description = "Expenses in the order they were added.")
    @ApiResponse(responseCode = "200", description = "Expenses in this group")
    public List<ExpenseResponse> list(@Parameter(description = "Group id") @PathVariable UUID groupId) {
        return expenses.list(groupId);
    }

    @GetMapping("/{expenseId}")
    @Operation(summary = "Get an expense", description = "One expense, including the rounded share of each participant.")
    @ApiResponse(responseCode = "200", description = "The expense",
            content = @Content(schema = @Schema(implementation = ExpenseResponse.class)))
    public ExpenseResponse get(@PathVariable UUID groupId, @PathVariable UUID expenseId) {
        return expenses.get(groupId, expenseId);
    }

    @DeleteMapping("/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an expense", description = "Removes the expense and its shares. Balances move with it.")
    @ApiResponse(responseCode = "204", description = "Expense deleted")
    public void delete(@PathVariable UUID groupId, @PathVariable UUID expenseId) {
        expenses.delete(groupId, expenseId);
    }
}
