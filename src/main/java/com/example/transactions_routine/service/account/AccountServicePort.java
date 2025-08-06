
package com.example.transactions_routine.service.account;

import com.example.transactions_routine.controller.account.AccountRequest;
import com.example.transactions_routine.model.Account;

public interface AccountServicePort {
    Account createAccount(AccountRequest accountRequest);
    Account findById(Long id);
}
