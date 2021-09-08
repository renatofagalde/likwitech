package br.com.likwi.techs.student;

import br.com.likwi.techs.exception.BadRequestException;
import br.com.likwi.techs.exception.StudentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Logger;

@Service
public class StudentService {

    final static Logger logger = Logger.getLogger(StudentService.class.toString());

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return this.studentRepository.findAll();
    }

    public Student add(Student student) {

        this.studentRepository.findByEmail(student.getEmail())
                .ifPresent(exists -> {
                    throw new BadRequestException("Email taken");
                });
        this.studentRepository.save(student);
        return student;
    }

    public void deleteStudent(Long studentId) {
        if (!this.studentRepository.existsById(studentId))
            throw new StudentNotFoundException(
                    MessageFormat.format("Student with id {0} does not exists", studentId));
        studentRepository.deleteById(studentId);
    }
}
