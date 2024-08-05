package dev.omerdanismaz.Cryptograph.repositories;

import dev.omerdanismaz.Cryptograph.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long>
{
    Optional<UserModel> findByDbfUserEmail(String userEmail);
}
