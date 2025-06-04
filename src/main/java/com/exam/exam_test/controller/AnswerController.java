// 答题控制器
package com.exam.exam_test.controller;

import com.exam.exam_test.entity.AnswerRecord;
import com.exam.exam_test.service.AnswerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answers")
public class AnswerController {
    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @PostMapping("/exam")
    public ResponseEntity<Void> submitExamAnswers(
            @RequestParam Long studentId,
            @RequestParam Long examId,
            @RequestBody List<AnswerRecord> answers) {
        answerService.submitExamAnswers(studentId, examId, answers);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/homework")
    public ResponseEntity<Void> submitHomeworkAnswers(
            @RequestParam Long studentId,
            @RequestParam Long homeworkId,
            @RequestBody List<AnswerRecord> answers) {
        answerService.submitHomeworkAnswers(studentId, homeworkId, answers);
        return ResponseEntity.ok().build();
    }
}
