package io.github.bjornvdlaan.examples.custom;

import io.github.bjornvdlaan.examples.domain.Customer;
import io.github.bjornvdlaan.examples.domain.OrderLine;
import io.github.bjornvdlaan.examples.domain.Product;
import io.github.bjornvdlaan.examples.domain.ProductCategory;
import io.github.bjornvdlaan.testvalues.TestNumber;
import io.github.bjornvdlaan.testvalues.TestString;
import io.github.bjornvdlaan.testvalues.TestValues;
import io.github.bjornvdlaan.testvalues.TestValuesContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Shows how to register domain-specific generators with constraints and how to
 * override built-in type generators (e.g. String → UUID).
 */
class CustomContextTest {

    private TestValuesContext context;

    @BeforeEach
    void setUp() {
        context = TestValues.builderWithDefaults()
                .register(Product.class, CustomContextTest::anyProduct)
                .register(Customer.class, CustomContextTest::anyAdultCustomer)
                .register(OrderLine.class, CustomContextTest::anyOrderLine)
                .build();
    }

    private static Product anyProduct() {
        return new Product(
                UUID.randomUUID().toString(),
                TestString.stringWith().minLength(3).maxLength(30).alphanumeric().get(),
                TestNumber.anyDouble(0.01, 999.99),
                TestValues.anyEnum(ProductCategory.class),
                TestNumber.anyInt(1, 100)
        );
    }

    private static Customer anyAdultCustomer() {
        return new Customer(
                UUID.randomUUID().toString(),
                TestString.stringWith().minLength(3).maxLength(20).alphanumeric().get(),
                TestString.anyEmail(),
                TestNumber.anyInt(18, 81),
                TestString.anyPhoneNumber()
        );
    }

    private static OrderLine anyOrderLine() {
        return new OrderLine(anyProduct(), TestNumber.anyInt(1, 11));
    }

    @Test
    void product_priceIsWithinDomainRange() {
        for (int i = 0; i < 20; i++) {
            Product product = context.anyOf(Product.class);
            assertThat(product.price()).isBetween(0.01, 999.99);
        }
    }

    @Test
    void customer_ageIsAlwaysAdult() {
        for (int i = 0; i < 20; i++) {
            Customer customer = context.anyOf(Customer.class);
            assertThat(customer.age()).isBetween(18, 80);
        }
    }

    @Test
    void orderLine_quantityIsWithinRange() {
        for (int i = 0; i < 20; i++) {
            OrderLine line = context.anyOf(OrderLine.class);
            assertThat(line.quantity()).isBetween(1, 10);
        }
    }

    @Test
    void overriddenStringGenerator_producesUUIDs() {
        TestValuesContext uuidStringContext = TestValues.builderWithDefaults()
                .register(String.class, () -> UUID.randomUUID().toString())
                .build();

        for (int i = 0; i < 10; i++) {
            String value = uuidStringContext.anyOf(String.class);
            assertThat(value).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        }
    }

    @Test
    void customContext_stillSupportsBuiltInTypes() {
        // Context with domain generators still resolves built-in types
        Integer value = context.anyOf(Integer.class);
        assertThat(value).isNotNull();
    }
}
