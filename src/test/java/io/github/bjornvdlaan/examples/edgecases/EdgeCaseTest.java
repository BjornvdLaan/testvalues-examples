package io.github.bjornvdlaan.examples.edgecases;

import io.github.bjornvdlaan.examples.domain.*;
import io.github.bjornvdlaan.examples.service.OrderService;
import io.github.bjornvdlaan.testvalues.TestNumber;
import io.github.bjornvdlaan.testvalues.TestString;
import io.github.bjornvdlaan.testvalues.TestValues;
import io.github.bjornvdlaan.testvalues.TestValuesContext;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Edge cases: boundary inputs, constructor guards, and nullable probability extremes.
 */
class EdgeCaseTest {

    private final OrderService orderService = new OrderService();

    // --- OrderService edge cases ---

    @Test
    void placeOrder_withEmptyLines_throwsIllegalArgument() {
        Customer adult = new Customer(UUID.randomUUID().toString(), "Jane", "jane@example.com", 25, "+1-555-000-0000");
        assertThatThrownBy(() -> orderService.placeOrder(adult, Collections.emptyList()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least one line");
    }

    @Test
    void placeOrder_withUnderageCustomer_throwsIllegalArgument() {
        Customer minor = new Customer(UUID.randomUUID().toString(), "Teen", "teen@example.com", 17, "+1-555-000-0000");
        List<OrderLine> lines = List.of(new OrderLine(
                new Product(UUID.randomUUID().toString(), "Widget", 9.99, ProductCategory.ELECTRONICS, 10),
                1
        ));
        assertThatThrownBy(() -> orderService.placeOrder(minor, lines))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("18");
    }

    // --- Domain constructor guards ---

    @Test
    void product_withNegativePrice_throwsIllegalArgument() {
        assertThatThrownBy(() ->
                new Product(UUID.randomUUID().toString(), "Bad Product", -0.01, ProductCategory.FOOD, 5)
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negative");
    }

    @Test
    void orderLine_withZeroQuantity_throwsIllegalArgument() {
        Product product = new Product(UUID.randomUUID().toString(), "Widget", 5.00, ProductCategory.BOOKS, 20);
        assertThatThrownBy(() -> new OrderLine(product, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void orderLine_withNegativeQuantity_throwsIllegalArgument() {
        Product product = new Product(UUID.randomUUID().toString(), "Widget", 5.00, ProductCategory.BOOKS, 20);
        assertThatThrownBy(() -> new OrderLine(product, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
    }

    // --- Nullable probability extremes ---

    @Test
    void anyNullableOf_withZeroProbability_neverReturnsNull() {
        TestValuesContext context = TestValues.builderWithDefaults().build();
        for (int i = 0; i < 50; i++) {
            String value = context.anyNullableOf(String.class, 0.0);
            assertThat(value).isNotNull();
        }
    }

    @Test
    void anyNullableOf_withFullProbability_alwaysReturnsNull() {
        TestValuesContext context = TestValues.builderWithDefaults().build();
        for (int i = 0; i < 50; i++) {
            String value = context.anyNullableOf(String.class, 1.0);
            assertThat(value).isNull();
        }
    }

    // --- Boundary ages ---

    @Test
    void placeOrder_withExactly18_succeeds() {
        Customer borderlineAdult = new Customer(
                UUID.randomUUID().toString(), "Adult", "adult@example.com", 18, "+1-555-000-0000"
        );
        List<OrderLine> lines = List.of(new OrderLine(
                new Product(UUID.randomUUID().toString(), "Widget", 9.99, ProductCategory.ELECTRONICS, 10),
                1
        ));
        Order order = orderService.placeOrder(borderlineAdult, lines);
        assertThat(order.status()).isEqualTo(OrderStatus.PENDING);
    }

    // --- TestNumber boundary ---

    @Test
    void anyDouble_atBoundaries_isWithinRange() {
        for (int i = 0; i < 30; i++) {
            double price = TestNumber.anyDouble(0.01, 999.99);
            assertThat(price).isGreaterThanOrEqualTo(0.01).isLessThan(999.99);
        }
    }

    @Test
    void anyInt_range_neverExceedsMax() {
        for (int i = 0; i < 30; i++) {
            int age = TestValues.anyInt(18, 81);
            assertThat(age).isBetween(18, 80); // max is exclusive
        }
    }

    // --- anyOf with single option ---

    @Test
    void anyOf_singleOption_alwaysReturnsThatOption() {
        for (int i = 0; i < 10; i++) {
            String result = TestValues.anyOf("only");
            assertThat(result).isEqualTo("only");
        }
    }
}
