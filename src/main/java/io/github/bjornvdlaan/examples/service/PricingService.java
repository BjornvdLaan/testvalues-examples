package io.github.bjornvdlaan.examples.service;

import io.github.bjornvdlaan.examples.domain.OrderLine;

import java.util.List;

public class PricingService {

    public double calculateTotal(List<OrderLine> lines) {
        return lines.stream()
                .mapToDouble(line -> line.product().price() * line.quantity())
                .sum();
    }

    public double applyVAT(double amount, double vatRate) {
        return amount * (1 + vatRate);
    }
}
