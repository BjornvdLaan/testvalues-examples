package io.github.bjornvdlaan.examples.service;

import io.github.bjornvdlaan.testvalues.TestNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class DiscountServiceTest {

    private DiscountService discountService;

    @BeforeEach
    void setUp() {
        discountService = new DiscountService();
    }

    // --- Bulk discount ---

    @Test
    void applyBulkDiscount_whenTotalAboveThreshold_returnsTenPercentDiscount() {
        double threshold = TestNumber.anyDouble(50.0, 200.0);
        double total = threshold + TestNumber.anyDouble(0.01, 100.0); // strictly above threshold

        double discount = discountService.applyBulkDiscount(total, threshold);

        assertThat(discount).isCloseTo(total * 0.10, within(0.001));
    }

    @Test
    void applyBulkDiscount_whenTotalBelowThreshold_returnsZero() {
        double threshold = TestNumber.anyDouble(100.0, 300.0);
        double total = TestNumber.anyDouble(1.0, 99.0); // strictly below any reasonable threshold

        // Make sure total < threshold
        double safeTotal = Math.min(total, threshold - 0.01);

        double discount = discountService.applyBulkDiscount(safeTotal, threshold);

        assertThat(discount).isZero();
    }

    @Test
    void applyBulkDiscount_whenTotalExactlyAtThreshold_returnsZero() {
        double threshold = TestNumber.anyDouble(50.0, 200.0);

        double discount = discountService.applyBulkDiscount(threshold, threshold);

        assertThat(discount).isZero();
    }

    // --- Loyalty discount ---

    @Test
    void applyLoyaltyDiscount_withFiveOrMoreOrders_returnsFivePercentDiscount() {
        double total = TestNumber.anyDouble(50.0, 500.0);
        int orderCount = TestNumber.anyInt(5, 21); // 5 to 20 orders

        double discount = discountService.applyLoyaltyDiscount(total, orderCount);

        assertThat(discount).isCloseTo(total * 0.05, within(0.001));
    }

    @Test
    void applyLoyaltyDiscount_withLessThanFiveOrders_returnsZero() {
        double total = TestNumber.anyDouble(50.0, 500.0);
        int orderCount = TestNumber.anyInt(0, 5); // 0 to 4 orders

        double discount = discountService.applyLoyaltyDiscount(total, orderCount);

        assertThat(discount).isZero();
    }

    @Test
    void applyLoyaltyDiscount_withExactlyFiveOrders_returnsFivePercent() {
        double total = TestNumber.anyDouble(50.0, 500.0);

        double discount = discountService.applyLoyaltyDiscount(total, 5);

        assertThat(discount).isCloseTo(total * 0.05, within(0.001));
    }

    @Test
    void applyLoyaltyDiscount_withZeroOrders_returnsZero() {
        double total = TestNumber.anyDouble(10.0, 200.0);

        double discount = discountService.applyLoyaltyDiscount(total, 0);

        assertThat(discount).isZero();
    }
}
