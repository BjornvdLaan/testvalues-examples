package io.github.bjornvdlaan.examples.builders;

import io.github.bjornvdlaan.examples.domain.*;
import io.github.bjornvdlaan.examples.shared.TestContext;
import io.github.bjornvdlaan.testvalues.TestNumber;
import io.github.bjornvdlaan.testvalues.TestString;
import io.github.bjornvdlaan.testvalues.TestValues;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Fluent builder for Order test fixtures. All fields default to random
 * context-generated values; individual fields can be overridden fluently.
 */
public class OrderTestBuilder {

    private String id = UUID.randomUUID().toString();
    private Customer customer = TestContext.DEFAULT.anyOf(Customer.class);
    private List<OrderLine> lines = defaultLines();
    private OrderStatus status = OrderStatus.PENDING;
    private LocalDate orderDate = LocalDate.now();

    public static OrderTestBuilder anOrder() {
        return new OrderTestBuilder();
    }

    public OrderTestBuilder withCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public OrderTestBuilder withLines(List<OrderLine> lines) {
        this.lines = lines;
        return this;
    }

    public OrderTestBuilder withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderTestBuilder withOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
        return this;
    }

    public Order build() {
        return new Order(id, customer, lines, status, orderDate);
    }

    private static List<OrderLine> defaultLines() {
        int count = TestNumber.anyInt(1, 4);
        List<OrderLine> lines = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            lines.add(TestContext.DEFAULT.anyOf(OrderLine.class));
        }
        return lines;
    }
}
