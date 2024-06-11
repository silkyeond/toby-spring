package toby.spring.test.template;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CalcSumTest {
  Calculator calculator;
  String numFilepath;

  @BeforeEach
  public void setUp() {
    this.calculator = new Calculator();
    this.numFilepath = getClass().getResource("/numbers.txt").getFile();
  }

  @Test
  public void sumOfNumbers() throws IOException {
    assertThat(calculator.calcSum(this.numFilepath)).isEqualTo(10);
  }

  @Test
  public void sumOfMultipleNumbers() throws IOException {
    assertThat(calculator.calcMultiply(this.numFilepath)).isEqualTo(24);
  }
}
