package dev.omerdanismaz.Cryptograph.services;

import dev.omerdanismaz.Cryptograph.enums.ESessionAttributes;
import dev.omerdanismaz.Cryptograph.iservices.IAccountService;
import dev.omerdanismaz.Cryptograph.models.AccountModel;
import dev.omerdanismaz.Cryptograph.repositories.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.omerdanismaz.Cryptograph.cryptography.CryptographyOperations.decryptData;
import static dev.omerdanismaz.Cryptograph.cryptography.CryptographyOperations.encryptData;
import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.getSessionAttribute;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements IAccountService
{
    private final AccountRepository accountRepository;

    @Override
    public void createAccount(AccountModel accountModel, HttpServletRequest httpServletRequest)
    {
        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_ID));
        String userEncryptionKey = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_ENCRYPTION_KEY);

        String plainAccountName = accountModel.getDbfAccountName();
        String plainAccountUsername = accountModel.getDbfAccountUsername();
        String plainAccountPassword = accountModel.getDbfAccountPassword();

        String encryptedAccountName = encryptData(plainAccountName, userEncryptionKey);
        String encryptedAccountUsername = encryptData(plainAccountUsername, userEncryptionKey);
        String encryptedAccountPassword = encryptData(plainAccountPassword, userEncryptionKey);

        accountModel.setDbfAccountUserId(userId);
        accountModel.setDbfAccountName(encryptedAccountName);
        accountModel.setDbfAccountUsername(encryptedAccountUsername);
        accountModel.setDbfAccountPassword(encryptedAccountPassword);
        accountModel.setDbfAccountCreatedOn(new Date());
        accountModel.setDbfAccountUpdatedOn(new Date());

        accountRepository.save(accountModel);
    }

    @Override
    public AccountModel readAccountById(Long accountId, HttpServletRequest httpServletRequest)
    {
        Optional<AccountModel> searchedAccount = accountRepository.findById(accountId);

        String userEncryptionKey = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_ENCRYPTION_KEY);

        if(searchedAccount.isPresent())
        {
            AccountModel accountModel = searchedAccount.get();

            String decryptedAccountName = decryptData
                    (accountModel.getDbfAccountName(), userEncryptionKey);
            String decryptedAccountUsername = decryptData
                    (accountModel.getDbfAccountUsername(), userEncryptionKey);
            String decryptedAccountPassword = decryptData
                    (accountModel.getDbfAccountPassword(), userEncryptionKey);

            accountModel.setDbfAccountName(decryptedAccountName);
            accountModel.setDbfAccountUsername(decryptedAccountUsername);
            accountModel.setDbfAccountPassword(decryptedAccountPassword);

            return accountModel;
        }

        return AccountModel
                .builder()
                .dbfAccountId(0L)
                .dbfAccountUserId(0L)
                .dbfAccountName("EMPTY")
                .dbfAccountUsername("EMPTY")
                .dbfAccountPassword("EMPTY")
                .dbfAccountCreatedOn(new Date())
                .dbfAccountUpdatedOn(new Date())
                .build();
    }

    @Override
    public List<AccountModel> readAllAccountsByUserId(Long userId, HttpServletRequest httpServletRequest)
    {
        List<AccountModel> allAccounts = accountRepository.findByDbfAccountUserId(userId);

        String userEncryptionKey = getSessionAttribute(httpServletRequest,
                ESessionAttributes.USER_ENCRYPTION_KEY);

        return allAccounts
                .stream()
                .peek(account -> {
                    account.setDbfAccountName(decryptData(account.getDbfAccountName(),
                            userEncryptionKey));
                    account.setDbfAccountUsername(decryptData(account.getDbfAccountUsername(),
                            userEncryptionKey));
                    account.setDbfAccountPassword(decryptData(account.getDbfAccountPassword(),
                            userEncryptionKey));
                })
                .sorted(Comparator.comparing(AccountModel::getDbfAccountName))
                .collect(Collectors.toList());
    }

    @Override
    public void updateAccount(AccountModel accountModel, HttpServletRequest httpServletRequest)
    {
        Optional<AccountModel> searchedAccount = accountRepository.findById(accountModel.getDbfAccountId());

        if(searchedAccount.isPresent())
        {
            AccountModel existingAccount =  searchedAccount.get();

            String userEncryptionKey = getSessionAttribute(httpServletRequest,
                    ESessionAttributes.USER_ENCRYPTION_KEY);

            if(accountModel.getDbfAccountName() != null)
            {
                String encryptedAccountName = encryptData(accountModel.getDbfAccountName(),
                        userEncryptionKey);
                existingAccount.setDbfAccountName(encryptedAccountName);
            }

            if(accountModel.getDbfAccountUsername() != null)
            {
                String encryptedAccountUsername = encryptData(accountModel.getDbfAccountUsername(),
                        userEncryptionKey);
                existingAccount.setDbfAccountUsername(encryptedAccountUsername);
            }

            if(accountModel.getDbfAccountPassword() != null)
            {
                String encryptedAccountPassword = encryptData(accountModel.getDbfAccountPassword(),
                        userEncryptionKey);
                existingAccount.setDbfAccountPassword(encryptedAccountPassword);
            }

            existingAccount.setDbfAccountUpdatedOn(new Date());

            accountRepository.save(existingAccount);
        }
    }

    @Override
    public void deleteAccount(AccountModel accountModel)
    {
        Optional<AccountModel> searchedAccount = accountRepository.findById(accountModel.getDbfAccountId());
        searchedAccount.ifPresent(accountRepository::delete);
    }
}
