package br.com.likwi.techs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class LikwiApplicationTests {

    Calculator calculator = new Calculator();

    @Test
    void itShouldAddTwoNumber() {
        //given
        int n1, n2;
        n1 = n2 = 1;

        //when
        final int result = calculator.add(n1, n2);

        //then
        assertThat(result).isEqualTo(2);
    }


    class Calculator {
        int add(int n1, int n2) {
            return n1 + n2;
        }
    }
}
