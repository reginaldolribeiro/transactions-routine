package com.example.transactions_routine.controller.account;

import com.example.transactions_routine.controller.ApiResponse;
import com.example.transactions_routine.service.account.AccountServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/v1/accounts")
@Validated
public class AccountController implements AccountApiDocs {

    private final AccountServicePort accountServicePort;

    public AccountController(AccountServicePort accountServicePort) {
        this.accountServicePort = accountServicePort;
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> save(@Valid @RequestBody AccountRequest accountRequest) {
        var savedAccount = accountServicePort.createAccount(accountRequest);
        var accountResponse = AccountResponse.fromDomain(savedAccount);

        var apiResponse = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Account created successfully.",
                accountResponse,
                null
        );

        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(accountResponse.id())
                .toUri();

        return ResponseEntity.created(location).body(apiResponse);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountResponse>> findById(@PathVariable Long id) {
        var account = accountServicePort.findById(id);
        var accountResponse = AccountResponse.fromDomain(account);
        var apiResponse = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Account found successfully.",
                accountResponse,
                null
        );
        return ResponseEntity.ok(apiResponse);
    }
}
