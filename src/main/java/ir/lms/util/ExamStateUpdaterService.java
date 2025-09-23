package ir.lms.util;

import ir.lms.model.ExamTemplate;
import ir.lms.model.enums.ExamState;
import ir.lms.repository.ExamRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ExamStateUpdaterService {

    private final ExamRepository examRepository;

    public ExamStateUpdaterService(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateExamStates() {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);

        List<ExamTemplate> exams = examRepository.findAll();

        for (ExamTemplate exam : exams) {
            LocalDate examDay = exam.getExamStartTime()
                    .atZone(zone)
                    .toLocalDate();

            ExamState newState;
            if (examDay.isAfter(today)) {
                newState = ExamState.NOT_STARTED;
            } else if (examDay.isEqual(today)) {
                newState = ExamState.STARTED;
            } else {
                newState = ExamState.FINISHED;
            }

            if (exam.getExamState() != newState) {
                exam.setExamState(newState);
                examRepository.save(exam);
            }
        }
    }
}
