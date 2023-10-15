package hochang.ecommerce.domain;

import hochang.ecommerce.constants.NumberConstants;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import static hochang.ecommerce.constants.NumberConstants.*;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity extends BaseTimeEntity {
    @CreatedBy
    @Column(updatable = false, length = INT_20)
    private String createdBy;

    @LastModifiedBy
    @Column(length = INT_20)
    private String modifiedBy;
}
