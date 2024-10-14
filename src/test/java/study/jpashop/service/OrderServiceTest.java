package study.jpashop.service;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import study.jpashop.domain.Address;
import study.jpashop.domain.Member;
import study.jpashop.domain.Order;
import study.jpashop.domain.OrderStatus;
import study.jpashop.domain.item.Book;
import study.jpashop.domain.item.Item;
import study.jpashop.exception.NotEnoughStockException;
import study.jpashop.repository.OrderRepository;

import static org.springframework.test.util.AssertionErrors.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void  상품주문() throws Exception {
        // given
        Member member = createMember();

        Item book = createBook("시골 JPA", 10000, 10);

        // when
        int orderCount =2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);


        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStaus());
        assertEquals("주문한 상품 졸유 수가 정확해야 한다.", 1,getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다", 10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야 한다.", 10, book.getStockQuantity());


    }



    @Test
    public void  주문취소() throws Exception {
        // given
        Member member = createMember();
        Book item = createBook("시골 JPA",10000,10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL 이다.", OrderStatus.CANCEL, getOrder.getStaus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.",10,item.getStockQuantity());

    }

    @Test
    public void  상품주문_재고수량초과() throws Exception {
        // given
        Member member = createMember();
        Item item = createBook("시골 jpa", 10000, 10);

        int orderCount =11;

        // when
        Assertions.assertThrows(NotEnoughStockException.class, () -> orderService.order(member.getId(), item.getId(), orderCount));


    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setStockQuantity(stockQuantity);
        book.setPrice(price);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","강가","123-123"));
        em.persist(member);
        return member;
    }

}