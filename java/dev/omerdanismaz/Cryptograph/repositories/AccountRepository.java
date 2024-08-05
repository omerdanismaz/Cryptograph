package dev.omerdanismaz.Cryptograph.repositories;

import dev.omerdanismaz.Cryptograph.models.AccountModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountModel, Long>
{
    List<AccountModel> findByDbfAccountUserId(Long accountUserId);
}
