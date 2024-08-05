package dev.omerdanismaz.Cryptograph.services;

import dev.omerdanismaz.Cryptograph.enums.EUserStatus;
import dev.omerdanismaz.Cryptograph.iservices.IUserService;
import dev.omerdanismaz.Cryptograph.models.UserModel;
import dev.omerdanismaz.Cryptograph.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static dev.omerdanismaz.Cryptograph.cryptography.CryptographyOperations.encryptData;
import static dev.omerdanismaz.Cryptograph.functions.GeneralOperations.generateRandomString;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService
{
    private final UserRepository userRepository;

    @Override
    public void createUser(UserModel userModel)
    {
        String plainPassword = userModel.getDbfUserPassword();
        String encryptedPassword = encryptData(plainPassword, plainPassword);

        String encryptionKey = generateRandomString(64);
        String encryptedEncryptionKey = encryptData(encryptionKey, plainPassword);

        userModel.setDbfUserPassword(encryptedPassword);
        userModel.setDbfUserEncryptionKey(encryptedEncryptionKey);
        userModel.setDbfUserCreatedOn(new Date());
        userModel.setDbfUserLoggedOn(new Date());
        userModel.setDbfUserStatus(EUserStatus.MEMBER);
        userRepository.save(userModel);
    }

    @Override
    public UserModel readUserById(Long userId)
    {
        Optional<UserModel> searchedUser = userRepository.findById(userId);

        return searchedUser.orElseGet(() -> UserModel.builder()
                .dbfUserId(0L)
                .dbfUserFirstName("EMPTY")
                .dbfUserLastName("EMPTY")
                .dbfUserEmail("EMPTY")
                .dbfUserPassword("EMPTY")
                .dbfUserEncryptionKey("EMPTY")
                .dbfUserCreatedOn(new Date())
                .dbfUserLoggedOn(new Date())
                .dbfUserStatus(EUserStatus.GUEST)
                .build());
    }

    @Override
    public UserModel readUserByEmail(String userEmail)
    {
        Optional<UserModel> searchedUser = userRepository.findByDbfUserEmail(userEmail);

        return searchedUser.orElseGet(() -> UserModel.builder()
                .dbfUserId(0L)
                .dbfUserFirstName("EMPTY")
                .dbfUserLastName("EMPTY")
                .dbfUserEmail("EMPTY")
                .dbfUserPassword("EMPTY")
                .dbfUserEncryptionKey("EMPTY")
                .dbfUserCreatedOn(new Date())
                .dbfUserLoggedOn(new Date())
                .dbfUserStatus(EUserStatus.GUEST)
                .build());
    }

    @Override
    public void updateUser(UserModel userModel)
    {
        Optional<UserModel> searchedUser = userRepository.findById(userModel.getDbfUserId());

        if(searchedUser.isPresent())
        {
            if(searchedUser.get().getDbfUserId() != 0L)
            {
                userRepository.save(searchedUser.get());
            }
        }
    }
}
