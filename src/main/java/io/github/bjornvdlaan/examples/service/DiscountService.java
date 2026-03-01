package io.github.bjornvdlaan.examples.service;

public class DiscountService {

    public double applyBulkDiscount(double total, double threshold) {
        if (total > threshold) {
            return total * 0.10;
        }
        return 0;
    }

    public double applyLoyaltyDiscount(double total, int customerOrderCount) {
        if (customerOrderCount >= 5) {
            return total * 0.05;
        }
        return 0;
    }
}
