package dev.omerdanismaz.Cryptograph.rest;

import dev.omerdanismaz.Cryptograph.enums.ECryptographyError;
import dev.omerdanismaz.Cryptograph.iservices.IUserService;
import dev.omerdanismaz.Cryptograph.models.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static dev.omerdanismaz.Cryptograph.cryptography.CryptographyOperations.decryptData;

@RestController
@AllArgsConstructor
@RequestMapping("/REST")
public class RESTController
{
    private final IUserService userService;

    @PostMapping("/checkUserExistence")
    public RESTResponse checkUserExistencePOST(@RequestParam("requestData") String requestData)
    {
        UserModel searchedUser = userService.readUserByEmail(requestData);

        if(!searchedUser.getDbfUserEmail().equals("EMPTY"))
        {
            RESTResponse restResponse = new RESTResponse();
            restResponse.setResponseData("RESPONSE");
            return restResponse;
        }

        return null;
    }

    @PostMapping("/checkUserPassword")
    public RESTResponse checkUserPasswordPOST(@RequestParam("requestDataOne") String requestDataOne,
                                              @RequestParam("requestDataTwo") String requestDataTwo)
    {
        UserModel searchedUser = userService.readUserByEmail(requestDataOne);

        if(!decryptData(searchedUser.getDbfUserPassword(), requestDataTwo)
                .equals(ECryptographyError.CRYPTOGRAPHY_ERROR.name()))
        {
            RESTResponse restResponse = new RESTResponse();
            restResponse.setResponseData("RESPONSE");
            return restResponse;
        }

        return null;
    }
}
