package io.github.bjornvdlaan.examples.basic;

import io.github.bjornvdlaan.examples.domain.OrderStatus;
import io.github.bjornvdlaan.examples.domain.ProductCategory;
import io.github.bjornvdlaan.testvalues.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates every major static method on TestValues, TestString, TestNumber,
 * TestDateTime, TestBoolean, and TestEnum with domain-appropriate assertions.
 */
class BasicValueGenerationTest {

    // --- TestValues ---

    @Test
    void anyOf_class_returnsNonNull() {
        String value = TestValues.anyOf(String.class);
        assertThat(value).isNotNull();
    }

    @Test
    void anyOf_options_picksOneOfTheSupplied() {
        String chosen = TestValues.anyOf("apple", "banana", "cherry");
        assertThat(chosen).isIn("apple", "banana", "cherry");
    }

    @Test
    void anyNullableOf_withLowProbability_returnsValueMostOfTheTime() {
        // With 0% null probability it must always return a value
        String value = TestValues.anyNullableOf(String.class, 0.0);
        assertThat(value).isNotNull();
    }

    @Test
    void anyNullableOf_withFullProbability_alwaysReturnsNull() {
        String value = TestValues.anyNullableOf(String.class, 1.0);
        assertThat(value).isNull();
    }

    // --- TestString ---

    @Test
    void anyString_returnsNonEmptyString() {
        String value = TestValues.anyString();
        assertThat(value).isNotEmpty();
    }

    @Test
    void anyString_withLength_returnsExactLength() {
        String value = TestValues.anyString(12);
        assertThat(value).hasSize(12);
    }

    @Test
    void anyEmail_hasAtSignAndDot() {
        String email = TestValues.anyEmail();
        assertThat(email).contains("@").contains(".");
    }

    @Test
    void stringWith_prefixSuffix_isRespected() {
        String value = TestString.stringWith().exactLength(15).prefix("test-").suffix("-end").get();
        assertThat(value).startsWith("test-").endsWith("-end");
    }

    @Test
    void anyPhoneNumber_matchesFormat() {
        String phone = TestString.anyPhoneNumber();
        assertThat(phone).matches("\\+\\d+-\\d{3}-\\d{3}-\\d{4}");
    }

    @Test
    void anyUuid_isWellFormed() {
        String uuid = TestString.anyUuid();
        assertThat(uuid).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    // --- TestNumber ---

    @Test
    void anyInt_returnsValue() {
        int value = TestValues.anyInt();
        // Just checking it doesn't throw; any int is valid
        assertThat(value).isInstanceOf(Integer.class);
    }

    @Test
    void anyInt_inRange_isWithinBounds() {
        int value = TestValues.anyInt(1, 101);
        assertThat(value).isBetween(1, 100);
    }

    @Test
    void anyDouble_inRange_isWithinBounds() {
        double value = TestNumber.anyDouble(0.01, 999.99);
        assertThat(value).isBetween(0.01, 999.99);
    }

    @Test
    void anyLong_returnsValue() {
        long value = TestValues.anyLong();
        assertThat(value).isInstanceOf(Long.class);
    }

    @Test
    void anyPositiveInt_isGreaterThanZero() {
        int value = TestNumber.anyPositiveInt();
        assertThat(value).isPositive();
    }

    // --- TestBoolean ---

    @Test
    void anyBoolean_returnsBoolean() {
        boolean value = TestValues.anyBoolean();
        assertThat(value).isIn(true, false);
    }

    @Test
    void anyBoolean_withProbabilityOne_alwaysTrue() {
        boolean value = TestBoolean.anyBoolean(1.0);
        assertThat(value).isTrue();
    }

    @Test
    void anyBoolean_withProbabilityZero_alwaysFalse() {
        boolean value = TestBoolean.anyBoolean(0.0);
        assertThat(value).isFalse();
    }

    // --- TestEnum ---

    @Test
    void anyEnum_productCategory_returnsValidCategory() {
        ProductCategory category = TestValues.anyEnum(ProductCategory.class);
        assertThat(category).isNotNull().isIn(ProductCategory.values());
    }

    @Test
    void anyEnum_orderStatus_returnsValidStatus() {
        OrderStatus status = TestValues.anyEnum(OrderStatus.class);
        assertThat(status).isNotNull().isIn(OrderStatus.values());
    }

    // --- TestDateTime ---

    @Test
    void anyLocalDate_returnsValidDate() {
        LocalDate date = TestValues.anyLocalDate();
        assertThat(date).isNotNull()
                .isAfterOrEqualTo(LocalDate.of(1970, 1, 1))
                .isBeforeOrEqualTo(LocalDate.of(2030, 12, 31));
    }

    @Test
    void anyPastLocalDate_isBeforeOrEqualToToday() {
        LocalDate past = TestDateTime.anyPastLocalDate();
        assertThat(past).isBeforeOrEqualTo(LocalDate.now());
    }

    @Test
    void anyFutureLocalDate_isAfterOrEqualToToday() {
        LocalDate future = TestDateTime.anyFutureLocalDate();
        assertThat(future).isAfterOrEqualTo(LocalDate.now());
    }
}
