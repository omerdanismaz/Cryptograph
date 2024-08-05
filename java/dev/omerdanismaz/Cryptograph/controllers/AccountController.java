package dev.omerdanismaz.Cryptograph.controllers;

import dev.omerdanismaz.Cryptograph.enums.ESessionAttributes;
import dev.omerdanismaz.Cryptograph.iservices.IAccountService;
import dev.omerdanismaz.Cryptograph.iservices.ISessionService;
import dev.omerdanismaz.Cryptograph.iservices.IUserService;
import dev.omerdanismaz.Cryptograph.models.AccountModel;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.*;
import static dev.omerdanismaz.Cryptograph.functions.SessionOperations.setDecryptedAccounts;

@Controller
@AllArgsConstructor
public class AccountController
{
    private final IUserService userService;
    private final ISessionService sessionService;
    private final IAccountService accountService;

    @GetMapping("/")
    public String indexGET(Model model,
                           HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        List<AccountModel> decryptedAccounts = getDecryptedAccounts(httpServletRequest, accountService);

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("decryptedAccounts",
                decryptedAccounts);

        return "/app/account/accounts";
    }

    @GetMapping("/addAccount")
    public String addAccountGET(Model model,
                                HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/login";
        }

        model.addAttribute("CSRFToken",
                getSessionAttribute(httpServletRequest, ESessionAttributes.CSRF_TOKEN));
        model.addAttribute("messageOne",
                "Account name is empty or too long. (Maximum Acceptable Length: 64)");
        model.addAttribute("messageTwo",
                "Account username is empty or too long. (Maximum Acceptable Length: 64)");
        model.addAttribute("messageThree",
                "Account password is empty or too long. (Maximum Acceptable Length: 64)");

        return "/app/account/addAccount";
    }

    @PostMapping("/addAccount")
    public String addAccountPOST(@RequestParam("CSRFToken") String CSRFToken,
                                 @RequestParam("accountName") String accountName,
                                 @RequestParam("accountUsername") String accountUsername,
                                 @RequestParam("accountPassword") String accountPassword,
                                 HttpServletRequest httpServletRequest)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(accountName.isEmpty() || accountName.length() > 64)
        {
            return "redirect:/";
        }

        if(accountUsername.isEmpty() || accountUsername.length() > 64)
        {
            return "redirect:/";
        }

        if(accountPassword.isEmpty() || accountPassword.length() > 64)
        {
            return "redirect:/";
        }

        AccountModel accountModel = AccountModel
                .builder()
                .dbfAccountName(accountName)
                .dbfAccountUsername(accountUsername)
                .dbfAccountPassword(accountPassword)
                .build();

        accountService.createAccount(accountModel, httpServletRequest);

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        List<AccountModel> decryptedAccounts = accountService.readAllAccountsByUserId
                (userId, httpServletRequest);
        setDecryptedAccounts(httpServletRequest, decryptedAccounts);

        return "redirect:/";
    }

    @PostMapping("/viewAccount")
    public String viewAccountPOST(@RequestParam("CSRFToken") String CSRFToken,
                                  @RequestParam("accountId") String accountId,
                                  Model model,
                                  HttpServletResponse httpServletResponse,
                                  HttpServletRequest httpServletRequest)
    {
        initializeSession(httpServletRequest, httpServletResponse, userService, sessionService);

        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(accountId.isEmpty())
        {
            return "redirect:/";
        }

        AccountModel searchedAccount = accountService.readAccountById
                (Long.parseLong(accountId), httpServletRequest);

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        if(searchedAccount.getDbfAccountUserId().equals(userId))
        {
            model.addAttribute("accountName", searchedAccount.getDbfAccountName());
            model.addAttribute("accountUsername", searchedAccount.getDbfAccountUsername());
            model.addAttribute("accountPassword", searchedAccount.getDbfAccountPassword());
            model.addAttribute("accountCreatedOn", searchedAccount.getDbfAccountCreatedOn());
            model.addAttribute("accountUpdatedOn", searchedAccount.getDbfAccountUpdatedOn());
            model.addAttribute("CSRFToken", CSRFToken);
            model.addAttribute("accountId", accountId);
        }

        return "/app/account/viewAccount";
    }

    @PostMapping("/updateAccount")
    public String updateAccountPOST(@RequestParam("CSRFToken") String CSRFToken,
                                    @RequestParam("accountId") String accountId,
                                    Model model,
                                    HttpServletRequest httpServletRequest)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(accountId.isEmpty())
        {
            return "redirect:/";
        }

        AccountModel searchedAccount = accountService.readAccountById
                (Long.parseLong(accountId), httpServletRequest);

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        if(searchedAccount.getDbfAccountUserId().equals(userId))
        {
            model.addAttribute("CSRFToken", CSRFToken);
            model.addAttribute("accountId", accountId);
            model.addAttribute("accountName", searchedAccount.getDbfAccountName());
            model.addAttribute("accountUsername", searchedAccount.getDbfAccountUsername());
            model.addAttribute("accountPassword", searchedAccount.getDbfAccountPassword());
            model.addAttribute("messageOne",
                    "Account name is empty or too long. (Maximum Acceptable Length: 64)");
            model.addAttribute("messageTwo",
                    "Account username is empty or too long. (Maximum Acceptable Length: 64)");
            model.addAttribute("messageThree",
                    "Account password is empty or too long. (Maximum Acceptable Length: 64)");
        }

        return "/app/account/updateAccount";
    }

    @PostMapping("/updateAccountProcess")
    public String updateAccountProcessPOST(@RequestParam("CSRFToken") String CSRFToken,
                                           @RequestParam("accountId") String accountId,
                                           @RequestParam("newAccountName") String newAccountName,
                                           @RequestParam("newAccountUsername") String newAccountUsername,
                                           @RequestParam("newAccountPassword") String newAccountPassword,
                                           HttpServletRequest httpServletRequest)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(accountId.isEmpty()
                || newAccountName.isEmpty() ||newAccountUsername.isEmpty() ||newAccountPassword.isEmpty())
        {
            return "redirect:/";
        }

        AccountModel searchedAccount = accountService.readAccountById
                (Long.parseLong(accountId), httpServletRequest);

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        if(searchedAccount.getDbfAccountUserId().equals(userId))
        {
            searchedAccount.setDbfAccountName(newAccountName);
            searchedAccount.setDbfAccountUsername(newAccountUsername);
            searchedAccount.setDbfAccountPassword(newAccountPassword);
            searchedAccount.setDbfAccountUpdatedOn(new Date());
            accountService.updateAccount(searchedAccount, httpServletRequest);

            List<AccountModel> decryptedAccounts = accountService.readAllAccountsByUserId
                    (userId, httpServletRequest);

            setDecryptedAccounts(httpServletRequest, decryptedAccounts);
        }

        return "redirect:/";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccountPOST(@RequestParam("CSRFToken") String CSRFToken,
                                    @RequestParam("accountId") String accountId,
                                    HttpServletRequest httpServletRequest)
    {
        if(isUserGuest(httpServletRequest))
        {
            return "redirect:/";
        }

        if(!isCSRFTokenValid(httpServletRequest, CSRFToken))
        {
            return "redirect:/";
        }

        if(accountId.isEmpty())
        {
            return "redirect:/";
        }

        AccountModel searchedAccount = accountService.readAccountById
                (Long.parseLong(accountId), httpServletRequest);

        Long userId = Long.parseLong(getSessionAttribute(httpServletRequest, ESessionAttributes.USER_ID));

        if(searchedAccount.getDbfAccountUserId().equals(userId))
        {
            accountService.deleteAccount(searchedAccount);

            List<AccountModel> decryptedAccounts = accountService.readAllAccountsByUserId
                    (userId, httpServletRequest);

            setDecryptedAccounts(httpServletRequest, decryptedAccounts);
        }

        return "redirect:/";
    }
}
