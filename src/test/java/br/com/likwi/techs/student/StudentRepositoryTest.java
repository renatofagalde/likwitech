package br.com.likwi.techs.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    @AfterEach
    void tearDown() {
        this.underTest.deleteAll();
    }

    @Test
    void itShouldCheckIfStudentExistsInFindByEmail() {
        //given
        final String email = "likwi01-test@likwi.com.br";
        final Student student01 = Student.builder()
                .name("likwi01-test")
                .email(email)
                .gender(Gender.MALE)
                .build();
        underTest.save(student01);

        //when
        final Optional<Student> expected = underTest.findByEmail(email);

        //then
        assertThat(expected.isPresent()).isTrue();
    }

    @Test
    void itShouldCheckWhenStudentInFindByEmailDoesNotExists() {
        //given
        final String email = "likwi01-test@likwi.com.br";

        //when
        final Optional<Student> expected = underTest.findByEmail(email);

        //then
        assertThat(expected.isPresent()).isFalse();

    }
}
