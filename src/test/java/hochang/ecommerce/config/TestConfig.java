package hochang.ecommerce.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import hochang.ecommerce.repository.ItemRepositoryCustom;
import hochang.ecommerce.repository.ItemRepositoryCustomImpl;
import hochang.ecommerce.repository.OrderRepositoryCustom;
import hochang.ecommerce.repository.OrderRepositoryCustomImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@TestConfiguration
public class TestConfig {
    @PersistenceContext
    private EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    public ItemRepositoryCustom itemRepositoryCustomImpl() {
        return new ItemRepositoryCustomImpl(jpaQueryFactory());
    }

    @Bean
    public OrderRepositoryCustom orderRepositoryCustomImpl() {
        return new OrderRepositoryCustomImpl(jpaQueryFactory());
    }
}
