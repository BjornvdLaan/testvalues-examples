package io.github.bjornvdlaan.examples.service;

import io.github.bjornvdlaan.examples.builders.OrderTestBuilder;
import io.github.bjornvdlaan.examples.domain.*;
import io.github.bjornvdlaan.examples.shared.TestContext;
import io.github.bjornvdlaan.testvalues.TestNumber;
import io.github.bjornvdlaan.testvalues.TestString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderServiceTest {

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    void placeOrder_withValidCustomerAndLines_returnsOrderWithPendingStatus() {
        Customer customer = TestContext.DEFAULT.anyOf(Customer.class); // age 18-80
        List<OrderLine> lines = List.of(TestContext.DEFAULT.anyOf(OrderLine.class));

        Order order = orderService.placeOrder(customer, lines);

        assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.customer()).isEqualTo(customer);
        assertThat(order.lines()).isEqualTo(lines);
        assertThat(order.id()).isNotBlank();
    }

    @Test
    void placeOrder_withUnderageCustomer_throwsIllegalArgument() {
        Customer minor = new Customer(
                UUID.randomUUID().toString(),
                TestString.stringWith().minLength(3).maxLength(10).alphanumeric().get(),
                TestString.anyEmail(),
                17,
                TestString.anyPhoneNumber()
        );
        List<OrderLine> lines = List.of(TestContext.DEFAULT.anyOf(OrderLine.class));

        assertThatThrownBy(() -> orderService.placeOrder(minor, lines))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void placeOrder_withEmptyLines_throwsIllegalArgument() {
        Customer customer = TestContext.DEFAULT.anyOf(Customer.class);

        assertThatThrownBy(() -> orderService.placeOrder(customer, Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancelOrder_whenPending_returnsCancelledOrder() {
        Order order = OrderTestBuilder.anOrder().withStatus(OrderStatus.PENDING).build();

        Order cancelled = orderService.cancelOrder(order);

        assertThat(cancelled.status()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(cancelled.id()).isEqualTo(order.id());
        assertThat(cancelled.customer()).isEqualTo(order.customer());
    }

    @Test
    void cancelOrder_whenConfirmed_returnsCancelledOrder() {
        Order order = OrderTestBuilder.anOrder().withStatus(OrderStatus.CONFIRMED).build();

        Order cancelled = orderService.cancelOrder(order);

        assertThat(cancelled.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrder_whenShipped_throwsIllegalState() {
        Order order = OrderTestBuilder.anOrder().withStatus(OrderStatus.SHIPPED).build();

        assertThatThrownBy(() -> orderService.cancelOrder(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancelOrder_whenDelivered_throwsIllegalState() {
        Order order = OrderTestBuilder.anOrder().withStatus(OrderStatus.DELIVERED).build();

        assertThatThrownBy(() -> orderService.cancelOrder(order))
                .isInstanceOf(IllegalStateException.class);
    }
}
