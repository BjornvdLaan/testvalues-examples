package io.github.bjornvdlaan.examples.builders;

import io.github.bjornvdlaan.examples.domain.*;
import io.github.bjornvdlaan.examples.shared.TestContext;
import io.github.bjornvdlaan.testvalues.TestNumber;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.github.bjornvdlaan.examples.builders.OrderTestBuilder.anOrder;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates the builder pattern for creating Order test fixtures with
 * context-backed random defaults and selective field overrides.
 */
class BuilderPatternTest {

    @Test
    void defaultBuild_producesValidOrder() {
        Order order = anOrder().build();

        assertThat(order.id()).isNotBlank();
        assertThat(order.customer()).isNotNull();
        assertThat(order.lines()).isNotEmpty();
        assertThat(order.status()).isNotNull();
        assertThat(order.orderDate()).isNotNull();
    }

    @Test
    void buildWithSpecificCustomer_usesProvidedCustomer() {
        Customer specificCustomer = new Customer(
                UUID.randomUUID().toString(),
                "Alice",
                "alice@example.com",
                30,
                "+1-555-123-4567"
        );

        Order order = anOrder().withCustomer(specificCustomer).build();

        assertThat(order.customer()).isEqualTo(specificCustomer);
        assertThat(order.customer().name()).isEqualTo("Alice");
        assertThat(order.customer().email()).isEqualTo("alice@example.com");
    }

    @Test
    void buildWithSpecificStatus_usesProvidedStatus() {
        Order order = anOrder().withStatus(OrderStatus.SHIPPED).build();

        assertThat(order.status()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void buildWithLargeNumberOfLines_stressTest() {
        List<OrderLine> manyLines = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            manyLines.add(TestContext.DEFAULT.anyOf(OrderLine.class));
        }

        Order order = anOrder().withLines(manyLines).build();

        assertThat(order.lines()).hasSize(50);
        assertThat(order.lines()).allSatisfy(line -> {
            assertThat(line.product()).isNotNull();
            assertThat(line.quantity()).isPositive();
        });
    }

    @Test
    void buildWithOverrides_otherFieldsStillRandom() {
        Customer fixedCustomer = TestContext.DEFAULT.anyOf(Customer.class);
        Order order = anOrder().withCustomer(fixedCustomer).withStatus(OrderStatus.CONFIRMED).build();

        // Customer and status are fixed
        assertThat(order.customer()).isEqualTo(fixedCustomer);
        assertThat(order.status()).isEqualTo(OrderStatus.CONFIRMED);

        // Other fields are still generated
        assertThat(order.id()).isNotBlank();
        assertThat(order.lines()).isNotEmpty();
        assertThat(order.orderDate()).isNotNull();
    }

    @Test
    void multipleBuilds_produceDifferentIds() {
        Order order1 = anOrder().build();
        Order order2 = anOrder().build();

        assertThat(order1.id()).isNotEqualTo(order2.id());
    }
}
