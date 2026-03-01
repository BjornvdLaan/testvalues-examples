package io.github.bjornvdlaan.examples.domain;

public record OrderLine(Product product, int quantity) {
    public OrderLine {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
    }
}
