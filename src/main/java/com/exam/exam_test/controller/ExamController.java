// 考试控制器
package com.exam.exam_test.controller;

import com.exam.exam_test.dto.*;
import com.exam.exam_test.entity.Exam;
import com.exam.exam_test.service.ExamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    public ResponseEntity<List<Exam>> getExamList(@RequestParam Long userId) {
        List<Exam> exams = examService.getExamList(userId);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamDetail> getExamDetail(@PathVariable Long examId) {
        ExamDetail detail = examService.getExamDetail(examId);
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/{examId}/start")
    public ResponseEntity<ExamPaper> startExam(@PathVariable Long examId,
                                               @RequestParam Long studentId) {
        ExamPaper paper = examService.startExam(examId, studentId);
        return ResponseEntity.ok(paper);
    }

    @PostMapping("/{examId}/submit")
    public ResponseEntity<Void> submitExam(@PathVariable Long examId,
                                           @RequestParam Long studentId,
                                           @RequestBody List<Answer> answers) {
        examService.submitExamAnswers(examId, studentId, answers);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{examId}/time-remaining")
    public ResponseEntity<Long> getExamTimeRemaining(@PathVariable Long examId,
                                                     @RequestParam Long studentId) {
        long remainingTime = examService.getTimeRemaining(examId, studentId);
        return ResponseEntity.ok(remainingTime);
    }
}
