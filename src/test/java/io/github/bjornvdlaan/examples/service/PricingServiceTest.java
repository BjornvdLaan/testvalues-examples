package io.github.bjornvdlaan.examples.service;

import io.github.bjornvdlaan.examples.domain.OrderLine;
import io.github.bjornvdlaan.examples.domain.Product;
import io.github.bjornvdlaan.examples.domain.ProductCategory;
import io.github.bjornvdlaan.testvalues.TestNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PricingServiceTest {

    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new PricingService();
    }

    @Test
    void calculateTotal_singleLine_returnsPriceTimesQuantity() {
        double price = TestNumber.anyDouble(1.0, 100.0);
        int quantity = TestNumber.anyInt(1, 11);
        Product product = new Product(UUID.randomUUID().toString(), "Widget", price, ProductCategory.ELECTRONICS, 50);
        List<OrderLine> lines = List.of(new OrderLine(product, quantity));

        double total = pricingService.calculateTotal(lines);

        assertThat(total).isCloseTo(price * quantity, within(0.001));
    }

    @Test
    void calculateTotal_multipleLines_returnsSumOfLineTotals() {
        double price1 = TestNumber.anyDouble(1.0, 50.0);
        int qty1 = TestNumber.anyInt(1, 6);
        double price2 = TestNumber.anyDouble(1.0, 50.0);
        int qty2 = TestNumber.anyInt(1, 6);

        Product p1 = new Product(UUID.randomUUID().toString(), "A", price1, ProductCategory.BOOKS, 10);
        Product p2 = new Product(UUID.randomUUID().toString(), "B", price2, ProductCategory.CLOTHING, 10);
        List<OrderLine> lines = List.of(new OrderLine(p1, qty1), new OrderLine(p2, qty2));

        double total = pricingService.calculateTotal(lines);
        double expected = price1 * qty1 + price2 * qty2;

        assertThat(total).isCloseTo(expected, within(0.001));
    }

    @Test
    void applyVAT_withZeroRate_returnsOriginalAmount() {
        double amount = TestNumber.anyDouble(10.0, 500.0);

        double result = pricingService.applyVAT(amount, 0.0);

        assertThat(result).isCloseTo(amount, within(0.001));
    }

    @Test
    void applyVAT_with21Percent_returnsTotalIncludingVAT() {
        double amount = TestNumber.anyDouble(10.0, 500.0);
        double vatRate = 0.21;

        double result = pricingService.applyVAT(amount, vatRate);

        assertThat(result).isCloseTo(amount * 1.21, within(0.001));
    }

    @Test
    void applyVAT_withArbitraryRate_appliesCorrectly() {
        double amount = TestNumber.anyDouble(1.0, 200.0);
        double vatRate = TestNumber.anyDouble(0.05, 0.30);

        double result = pricingService.applyVAT(amount, vatRate);

        assertThat(result).isCloseTo(amount * (1 + vatRate), within(0.001));
    }

    @Test
    void calculateTotal_manyLines_returnsCorrectSum() {
        List<OrderLine> lines = new ArrayList<>();
        double expectedTotal = 0;
        for (int i = 0; i < 10; i++) {
            double price = TestNumber.anyDouble(1.0, 100.0);
            int quantity = TestNumber.anyInt(1, 6);
            Product product = new Product(UUID.randomUUID().toString(), "Item" + i, price, ProductCategory.SPORTS, 100);
            lines.add(new OrderLine(product, quantity));
            expectedTotal += price * quantity;
        }

        double total = pricingService.calculateTotal(lines);

        assertThat(total).isCloseTo(expectedTotal, within(0.001));
    }
}
