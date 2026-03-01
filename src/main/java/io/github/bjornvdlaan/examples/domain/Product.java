package io.github.bjornvdlaan.examples.domain;

public record Product(String id, String name, double price, ProductCategory category, int stockLevel) {
    public Product {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");
    }
}
