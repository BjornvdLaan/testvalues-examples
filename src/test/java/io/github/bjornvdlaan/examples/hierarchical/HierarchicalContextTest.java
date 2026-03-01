package io.github.bjornvdlaan.examples.hierarchical;

import io.github.bjornvdlaan.examples.domain.*;
import io.github.bjornvdlaan.testvalues.TestNumber;
import io.github.bjornvdlaan.testvalues.TestString;
import io.github.bjornvdlaan.testvalues.TestValues;
import io.github.bjornvdlaan.testvalues.TestValuesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates a three-level context hierarchy:
 *   1. Company context — base Product and Customer generators
 *   2. Order context  — child of company; adds OrderLine and Order generators
 *   3. VIP context    — child of order; overrides Customer (age >= 30, premium email)
 *
 * Child contexts inherit parent generators for types they do not override.
 */
class HierarchicalContextTest {

    private TestValuesContext companyContext;
    private TestValuesContext orderContext;
    private TestValuesContext vipContext;

    @BeforeEach
    void setUp() {
        // Level 1: company-wide base generators
        companyContext = TestValues.builderWithDefaults()
                .register(Product.class, () -> new Product(
                        UUID.randomUUID().toString(),
                        TestString.stringWith().minLength(3).maxLength(30).alphanumeric().get(),
                        TestNumber.anyDouble(1.00, 499.99),
                        TestValues.anyEnum(ProductCategory.class),
                        TestNumber.anyInt(10, 500)
                ))
                .register(Customer.class, () -> new Customer(
                        UUID.randomUUID().toString(),
                        TestString.stringWith().minLength(3).maxLength(20).alphanumeric().get(),
                        TestString.anyEmail(),
                        TestNumber.anyInt(18, 81),
                        TestString.anyPhoneNumber()
                ))
                .build();

        // Level 2: order context extends company context
        orderContext = companyContext.childBuilder()
                .register(OrderLine.class, () -> new OrderLine(
                        new Product(
                                UUID.randomUUID().toString(),
                                TestString.stringWith().minLength(3).maxLength(30).alphanumeric().get(),
                                TestNumber.anyDouble(1.00, 499.99),
                                TestValues.anyEnum(ProductCategory.class),
                                TestNumber.anyInt(1, 50)
                        ),
                        TestNumber.anyInt(1, 6)
                ))
                .register(Order.class, () -> {
                    Customer customer = new Customer(
                            UUID.randomUUID().toString(),
                            TestString.stringWith().minLength(3).maxLength(20).alphanumeric().get(),
                            TestString.anyEmail(),
                            TestNumber.anyInt(18, 81),
                            TestString.anyPhoneNumber()
                    );
                    return new Order(
                            UUID.randomUUID().toString(),
                            customer,
                            List.of(new OrderLine(
                                    new Product(
                                            UUID.randomUUID().toString(),
                                            "Product",
                                            TestNumber.anyDouble(1.00, 499.99),
                                            ProductCategory.ELECTRONICS,
                                            10
                                    ),
                                    TestNumber.anyInt(1, 6)
                            )),
                            OrderStatus.PENDING,
                            LocalDate.now()
                    );
                })
                .build();

        // Level 3: VIP context overrides Customer only
        vipContext = orderContext.childBuilder()
                .register(Customer.class, () -> new Customer(
                        UUID.randomUUID().toString(),
                        TestString.stringWith().minLength(3).maxLength(20).alphanumeric().get(),
                        TestString.stringWith().minLength(5).maxLength(10).alphanumeric().get().toLowerCase()
                                + "@premium.com",
                        TestNumber.anyInt(30, 71),
                        TestString.anyPhoneNumber()
                ))
                .build();
    }

    @Test
    void vipContext_customer_hasAgeAtLeast30() {
        for (int i = 0; i < 20; i++) {
            Customer customer = vipContext.anyOf(Customer.class);
            assertThat(customer.age()).isGreaterThanOrEqualTo(30);
        }
    }

    @Test
    void vipContext_customer_hasPremiumEmailDomain() {
        for (int i = 0; i < 20; i++) {
            Customer customer = vipContext.anyOf(Customer.class);
            assertThat(customer.email()).endsWith("@premium.com");
        }
    }

    @Test
    void vipContext_product_usesCompanyLevelGenerator() {
        // VIP context doesn't override Product — should fall back to company context
        for (int i = 0; i < 10; i++) {
            Product product = vipContext.anyOf(Product.class);
            assertThat(product.price()).isBetween(1.00, 499.99);
            assertThat(product.stockLevel()).isBetween(10, 499);
        }
    }

    @Test
    void orderContext_inheritsProduct_fromCompanyContext() {
        Product product = orderContext.anyOf(Product.class);
        assertThat(product.price()).isBetween(1.00, 499.99);
    }

    @Test
    void companyContext_doesNotKnowAboutOrderLines() {
        assertThat(companyContext.hasGenerator(OrderLine.class)).isFalse();
    }

    @Test
    void orderContext_knowsAboutOrderLines() {
        assertThat(orderContext.hasGenerator(OrderLine.class)).isTrue();
    }

    @Test
    void vipContext_inheritsOrderLines_fromOrderContext() {
        assertThat(vipContext.hasGenerator(OrderLine.class)).isTrue();
        OrderLine line = vipContext.anyOf(OrderLine.class);
        assertThat(line.quantity()).isBetween(1, 5);
    }
}
