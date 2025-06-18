// 考试控制器
package com.exam.exam_system.controller;

import com.exam.exam_system.dto.*;
import com.exam.exam_system.entity.Exam;
import com.exam.exam_system.service.ExamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    public ResponseEntity<List<ExamDTO>> getExamList(@RequestParam Long userId) {
        List<ExamDTO> exams = examService.getExamList(userId);
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamDetail> getExamDetail(@PathVariable Long examId) {
        ExamDetail detail = examService.getExamDetail(examId);
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/{examId}/start")
    public ResponseEntity<?> startExam(@PathVariable Long examId,
                                       @RequestParam Long studentId) {
        try {
            ExamPaper paper = examService.startExam(examId, studentId);
            return ResponseEntity.ok(paper);
        } catch (RuntimeException e) {
            // 返回更详细的错误信息
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
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
