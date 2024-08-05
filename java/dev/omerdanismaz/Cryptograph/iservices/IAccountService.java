package dev.omerdanismaz.Cryptograph.iservices;

import dev.omerdanismaz.Cryptograph.models.AccountModel;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IAccountService
{
    void createAccount(AccountModel accountModel, HttpServletRequest httpServletRequest);
    AccountModel readAccountById(Long accountId, HttpServletRequest httpServletRequest);
    List<AccountModel> readAllAccountsByUserId(Long userId, HttpServletRequest httpServletRequest);
    void updateAccount(AccountModel accountModel, HttpServletRequest httpServletRequest);
    void deleteAccount(AccountModel accountModel);
}
