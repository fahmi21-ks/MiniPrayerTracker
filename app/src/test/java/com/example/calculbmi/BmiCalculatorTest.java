package com.example.calculbmi;

import org.junit.Test;
import static org.junit.Assert.*;

public class BmiCalculatorTest {

    private final BmiCalculator bmiCalculator = new BmiCalculator();

    @Test
    public void getBmiCategory_underweight() {
        assertEquals("Underweight", bmiCalculator.getBmiCategory(18.4));
    }

    @Test
    public void getBmiCategory_normal() {
        assertEquals("Normal", bmiCalculator.getBmiCategory(24.9));
    }

    @Test
    public void getBmiCategory_overweight() {
        assertEquals("Overweight", bmiCalculator.getBmiCategory(29.9));
    }

    @Test
    public void getBmiCategory_obese() {
        assertEquals("Obese", bmiCalculator.getBmiCategory(30.0));
    }
}