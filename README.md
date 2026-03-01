# testvalues-examples

Practical examples for the [testvalues](https://github.com/BjornvdLaan/testvalues) library, a Java utility that generates randomised test values on every run to make your tests more robust.

The examples use an e-commerce domain (products, customers, orders) with real business logic, so you can see how testvalues fits into realistic unit tests.

## Prerequisites

- Java 17+
- Gradle (wrapper included)

## Running the tests

```bash
./gradlew test
```

## What's inside

| Package | What it demonstrates |
|---|---|
| `basic` | Every static method on `TestValues`, `TestString`, `TestNumber`, `TestDateTime`, `TestBoolean`, and `TestEnum` |
| `custom` | Registering domain-specific generators with constraints; overriding built-in generators |
| `hierarchical` | Three-level context inheritance - company → order → VIP |
| `edgecases` | Boundary inputs, constructor guards, nullable probability extremes |
| `builders` | Test builder pattern with context-backed random defaults and fluent overrides |
| `service` | Service-layer tests for `OrderService`, `PricingService`, and `DiscountService` |

## Dependency

```groovy
testImplementation 'io.github.bjornvdlaan:testvalues:0.1.0'
```
