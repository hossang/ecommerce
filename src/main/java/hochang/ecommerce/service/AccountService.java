package hochang.ecommerce.service;

import hochang.ecommerce.domain.Account;
import hochang.ecommerce.domain.User;
import hochang.ecommerce.dto.OrderAccount;
import hochang.ecommerce.repository.AccountRepository;
import hochang.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderAccount save(String username, OrderAccount orderAccount) {
        User user = userRepository.findByUsername(username);
        Account account = createAccount(user, orderAccount);
        accountRepository.save(account);
        return toOrderAccount(account);
    }

    private Account createAccount(User user, OrderAccount orderAccount) {
        return Account.builder()
                .user(user)
                .bank(orderAccount.getBank())
                .accountNumber(orderAccount.getAccountNumber())
                .balance(orderAccount.getBalance())
                .accountHolder(orderAccount.getAccountHolder())
                .build();

    }

    public List<OrderAccount> findOrderAccounts(String username) {
        User user = userRepository.findByUsername(username);
        List<Account> accounts = accountRepository.findByUserId(user.getId());
        return accounts.stream()
                .map(this::toOrderAccount)
                .collect(Collectors.toList());
    }

    public Account findAccount(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(EntityNotFoundException::new);
    }

    private OrderAccount toOrderAccount(Account account) {
        OrderAccount orderAccount = new OrderAccount();
        orderAccount.setId(account.getId());
        orderAccount.setBank(account.getBank());
        orderAccount.setAccountNumber(account.getAccountNumber());
        orderAccount.setBalance(account.getBalance());
        orderAccount.setAccountHolder(account.getAccountHolder());
        return orderAccount;
    }
}
