package hochang.ecommerce.service;

import hochang.ecommerce.domain.User;
import hochang.ecommerce.domain.ShippingAddress;
import hochang.ecommerce.dto.OrderAddress;
import hochang.ecommerce.repository.ShippingAddressRepository;
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
public class ShippingAddressService {
    private final ShippingAddressRepository shippingAddressRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderAddress save(User user, OrderAddress orderAddress) {
        ShippingAddress shippingAddress = createShippingAddress(orderAddress, user);
        shippingAddressRepository.save(shippingAddress);
        return toOrderAddress(shippingAddress);
    }

    public List<OrderAddress> findOrderAddresses(User user) {
        List<ShippingAddress> shippingAddresses = shippingAddressRepository.findByUserId(user.getId());
        return shippingAddresses.stream()
                .map(this::toOrderAddress)
                .collect(Collectors.toList());
    }

    public ShippingAddress findShippingAddress(Long shippingAddressId) {
        return shippingAddressRepository.findById(shippingAddressId).orElseThrow(EntityNotFoundException::new);
    }

    private ShippingAddress createShippingAddress(OrderAddress orderAddress, User user) {
        return ShippingAddress.builder()
                .user(user)
                .postCode(orderAddress.getPostCode())
                .address(orderAddress.getAddress())
                .detailAddress(orderAddress.getDetailAddress())
                .build();
    }

    private OrderAddress toOrderAddress(ShippingAddress shippingAddress) {
        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setId(shippingAddress.getId());
        orderAddress.setAddress(shippingAddress.getAddress());
        orderAddress.setDetailAddress(shippingAddress.getDetailAddress());
        return orderAddress;
    }
}
