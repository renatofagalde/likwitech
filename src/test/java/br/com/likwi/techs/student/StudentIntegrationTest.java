package br.com.likwi.techs.student;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-it.properties"
)
@AutoConfigureMockMvc
public class StudentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    private final Faker faker = new Faker();

    private final String URI = "/api/v001/students";

    final static Logger logger = Logger.getLogger(StudentIntegrationTest.class.toString());

    @Test
    void canRegisterNewStudent() throws Exception {


        logger.info(String.format("\t*********************** StudentIT \"canRegisterNewStudent\" *********************** \t"));


        //given
        final String name = String.format("%s %s",
                faker.name().firstName(),
                faker.name().lastName());

        final Student student = Student.builder()
                .name(name)
                .email(String.format("%s@likwi.com.br",
                        StringUtils.trimAllWhitespace(name.toLowerCase(Locale.ROOT))))
                .gender(Gender.MALE)
                .build();
        //when
        final ResultActions resultActions = mockMvc.perform(post(URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)));
        //then
        resultActions.andExpect(status().isOk());

        final List<Student> students = studentRepository.findAll();
        assertThat(students)
                .usingElementComparatorIgnoringFields("id")
                .contains(student);
    }

    @Test
    void canDeleteStudent() throws Exception {
        logger.info(String.format("\t*********************** StudentIT \"canDeleteStudent\" *********************** \t"));

        // given
        String name = String.format(
                "%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        String email = String.format("%s@likwi.com.br",
                StringUtils.trimAllWhitespace(name.toLowerCase()));

        Student student = new Student(
                name,
                email,
                Gender.FEMALE
        );

        mockMvc.perform(post(URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk());

        MvcResult getStudentsResult = mockMvc.perform(get(URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = getStudentsResult
                .getResponse()
                .getContentAsString();

        List<Student> students = objectMapper.readValue(
                contentAsString,
                new TypeReference<>() {
                }
        );

        long id = students.stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .map(Student::getId)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "student with email: " + email + " not found"));

        // when
        ResultActions resultActions = mockMvc
                .perform(delete(URI.concat("/") + id));

        // then
        resultActions.andExpect(status().isOk());
        boolean exists = studentRepository.existsById(id);
        assertThat(exists).isFalse();
    }
}
