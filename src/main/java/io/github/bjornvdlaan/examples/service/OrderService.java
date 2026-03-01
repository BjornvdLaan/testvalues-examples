package io.github.bjornvdlaan.examples.service;

import io.github.bjornvdlaan.examples.domain.Customer;
import io.github.bjornvdlaan.examples.domain.Order;
import io.github.bjornvdlaan.examples.domain.OrderLine;
import io.github.bjornvdlaan.examples.domain.OrderStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class OrderService {

    public Order placeOrder(Customer customer, List<OrderLine> lines) {
        if (customer.age() < 18) {
            throw new IllegalArgumentException("Customer must be at least 18 years old");
        }
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one line");
        }
        return new Order(
                UUID.randomUUID().toString(),
                customer,
                lines,
                OrderStatus.PENDING,
                LocalDate.now()
        );
    }

    public Order cancelOrder(Order order) {
        if (order.status() == OrderStatus.SHIPPED || order.status() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel an order that has been shipped or delivered");
        }
        return new Order(order.id(), order.customer(), order.lines(), OrderStatus.CANCELLED, order.orderDate());
    }
}
