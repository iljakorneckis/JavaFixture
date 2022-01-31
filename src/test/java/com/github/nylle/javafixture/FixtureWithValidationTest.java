package com.github.nylle.javafixture;

import com.github.nylle.javafixture.annotations.fixture.TestWithFixture;
import com.github.nylle.javafixture.testobjects.TestObjectWithJavaxValidationAnnotations;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;

import static org.assertj.core.api.Assertions.assertThat;

public class FixtureWithValidationTest {

    @Test
    void javaxSizeAnnotationIsSupported() {
        var sut = new Fixture().create(TestObjectWithJavaxValidationAnnotations.class);

        assertThat(sut.getWithMinAnnotation().length()).isGreaterThanOrEqualTo(5);
        assertThat(sut.getWithMaxAnnotation().length()).isLessThanOrEqualTo(100);
        assertThat(sut.getWithMinMaxAnnotation().length()).isBetween(3, 6);
    }

    @TestWithFixture
    void fixtureWillAlwaysCreateValidObject(TestObjectWithJavaxValidationAnnotations sut) {
        var factory = Validation.buildDefaultValidatorFactory();
        var validator = factory.getValidator();
        var violations = validator.validate(sut);
        assertThat( violations ).isEmpty();



    }

}
