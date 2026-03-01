package io.github.bjornvdlaan.examples.shared;

import io.github.bjornvdlaan.examples.domain.*;
import io.github.bjornvdlaan.testvalues.TestNumber;
import io.github.bjornvdlaan.testvalues.TestString;
import io.github.bjornvdlaan.testvalues.TestValues;
import io.github.bjornvdlaan.testvalues.TestValuesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TestContext {

    public static final TestValuesContext DEFAULT = TestValues.builderWithDefaults()
            .register(ProductCategory.class, () -> TestValues.anyEnum(ProductCategory.class))
            .register(OrderStatus.class, () -> TestValues.anyEnum(OrderStatus.class))
            .register(Product.class, () -> new Product(
                    UUID.randomUUID().toString(),
                    TestString.stringWith().minLength(3).maxLength(30).alphanumeric().get(),
                    TestNumber.anyDouble(0.01, 999.99),
                    TestValues.anyEnum(ProductCategory.class),
                    TestNumber.anyInt(0, 1000)
            ))
            .register(Customer.class, () -> new Customer(
                    UUID.randomUUID().toString(),
                    TestString.stringWith().minLength(3).maxLength(20).alphanumeric().get(),
                    TestString.anyEmail(),
                    TestNumber.anyInt(18, 81),
                    TestString.anyPhoneNumber()
            ))
            .register(OrderLine.class, () -> new OrderLine(
                    new Product(
                            UUID.randomUUID().toString(),
                            TestString.stringWith().minLength(3).maxLength(30).alphanumeric().get(),
                            TestNumber.anyDouble(0.01, 999.99),
                            TestValues.anyEnum(ProductCategory.class),
                            TestNumber.anyInt(1, 100)
                    ),
                    TestNumber.anyInt(1, 11)
            ))
            .register(Order.class, () -> {
                Customer customer = new Customer(
                        UUID.randomUUID().toString(),
                        TestString.stringWith().minLength(3).maxLength(20).alphanumeric().get(),
                        TestString.anyEmail(),
                        TestNumber.anyInt(18, 81),
                        TestString.anyPhoneNumber()
                );
                List<OrderLine> lines = List.of(
                        new OrderLine(
                                new Product(
                                        UUID.randomUUID().toString(),
                                        TestString.stringWith().minLength(3).maxLength(30).alphanumeric().get(),
                                        TestNumber.anyDouble(0.01, 999.99),
                                        TestValues.anyEnum(ProductCategory.class),
                                        TestNumber.anyInt(1, 100)
                                ),
                                TestNumber.anyInt(1, 11)
                        )
                );
                return new Order(
                        UUID.randomUUID().toString(),
                        customer,
                        lines,
                        TestValues.anyEnum(OrderStatus.class),
                        LocalDate.now()
                );
            })
            .build();

    private TestContext() {}
}
