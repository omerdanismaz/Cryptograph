package dev.omerdanismaz.Cryptograph.iservices;

import dev.omerdanismaz.Cryptograph.models.UserModel;

public interface IUserService
{
    void createUser(UserModel userModel);
    UserModel readUserById(Long userId);
    UserModel readUserByEmail(String userEmail);
    void updateUser(UserModel userModel);
}
