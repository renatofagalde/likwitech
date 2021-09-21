package br.com.likwi.techs.student;

import br.com.likwi.techs.exception.BadRequestException;
import br.com.likwi.techs.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    private StudentService underTest;

    @BeforeEach
    void setUp() {
        this.underTest = new StudentService(this.studentRepository);
    }

    @Test
    void canGetAllStudents() {
        //when
        underTest.getAllStudents(); //the mock was invoke is the method inside the getAllStudents

        //then
        verify(this.studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        //given
        final Student student = Student.builder()
                .name("likwi01-test")
                .email("likwi01-test@likwi.com.br")
                .gender(Gender.MALE)
                .build();

        //when
        this.underTest.add(student);

        //then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(this.studentRepository).save(studentArgumentCaptor.capture());

        final Student capturesStudent = studentArgumentCaptor.getValue();
        assertThat(capturesStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        //given
        final Student student = Student.builder()
                .name("likwi01-test")
                .email("likwi01-test@likwi.com.br")
                .gender(Gender.MALE)
                .build();

        given(this.studentRepository.findByEmail(student.getEmail()))
                .willReturn(Optional.of(new Student()));

        //when
        //then
        assertThatThrownBy(() -> this.underTest.add(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email " + student.getEmail() + " taken");

        verify(this.studentRepository, never()).save(any());

    }

    @Test
    void canDeleteStudent() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(true);
        // when
        underTest.deleteStudent(id);

        // then
        verify(studentRepository).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteStudentNotFound() {
        // given
        long id = 10;
        given(studentRepository.existsById(id))
                .willReturn(false);
        // when
        // then
        assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        verify(studentRepository, never()).deleteById(any());
    }
}
