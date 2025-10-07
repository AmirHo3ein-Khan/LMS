package ir.lms.aop;

import ir.lms.mapper.*;
import ir.lms.model.*;
import org.aspectj.weaver.patterns.PerObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MapperConfig {

    @Autowired
    public MapperConfig(MapperRegistry registry,
                        PersonMapper personMapper,
                        MajorMapper majorMapper,
                        CourseMapper courseMapper,
                        ExamMapper examMapper,
                        AnswerMapper answerMapper,
                        OfferedCourseMapper offeredCourseMapper,
                        OptionMapper optionMapper,
                        QuestionMapper questionMapper,
                        RegisterMapper registerMapper,
                        ResponseOfferedCourseMapper responseOfferedCourseMapper,
                        TermMapper termMapper) {
        registry.register(Person.class, personMapper);
        registry.register(Major.class, majorMapper);
        registry.register(Course.class, courseMapper);
        registry.register(ExamTemplate.class, examMapper);
        registry.register(OfferedCourse.class, offeredCourseMapper);
        registry.register(Option.class, optionMapper);
        registry.register(Question.class, questionMapper);
        registry.register(Term.class, termMapper);
        registry.register(OfferedCourse.class, responseOfferedCourseMapper);
        registry.register(Person.class, registerMapper);

    }
}
