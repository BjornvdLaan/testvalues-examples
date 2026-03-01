package io.github.bjornvdlaan.examples.domain;

import java.time.LocalDate;
import java.util.List;

public record Order(String id, Customer customer, List<OrderLine> lines, OrderStatus status, LocalDate orderDate) {}
