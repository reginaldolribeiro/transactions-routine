package com.example.transactions_routine.service.account;

import com.example.transactions_routine.controller.account.AccountRequest;
import com.example.transactions_routine.model.Account;
import com.example.transactions_routine.repository.AccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AccountService implements AccountServicePort {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public Account createAccount(AccountRequest accountRequest) {
        try {
            var account = Account.builder()
                    .documentNumber(accountRequest.documentNumber())
                    .build();
            return accountRepository.save(account);
        } catch (DataIntegrityViolationException e) {
            throw new AccountDocumentAlreadyExistsException("An account with document number '" + accountRequest.documentNumber() + "' already exists");
        }

    }

    @Override
    public Account findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
    }
}
