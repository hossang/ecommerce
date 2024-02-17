package hochang.ecommerce.repository;

import hochang.ecommerce.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);

    Account findByAccountNumber(String accountNumber);
}
