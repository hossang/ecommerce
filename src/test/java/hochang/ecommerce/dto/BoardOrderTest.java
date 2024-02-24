package hochang.ecommerce.dto;

import hochang.ecommerce.domain.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class BoardOrderTest {

    @Test
    @DisplayName("띄어쓰기 확인")
    public void checkWordSpacing() {
        //Given
        StringBuffer sb = new StringBuffer();
        sb.append("주문번호, ");
        //When
        sb.delete(sb.length() - 2, sb.length() - 1);
        sb.append("1");
        //Then
        System.out.println("sb.toString() = " + sb.toString());
    }
}