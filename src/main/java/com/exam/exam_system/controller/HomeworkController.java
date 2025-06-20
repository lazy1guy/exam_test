package com.exam.exam_system.controller;

import com.exam.exam_system.dto.*;
import com.exam.exam_system.service.HomeworkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/homeworks")
public class HomeworkController {
    private final HomeworkService homeworkService;

    public HomeworkController(HomeworkService homeworkService) {
        this.homeworkService = homeworkService;
    }

    @GetMapping
    public ResponseEntity<List<HomeworkDTO>> getHomeworkList(@RequestParam Long userId) {
        List<HomeworkDTO> homeworks = homeworkService.getHomeworkList(userId);
        return ResponseEntity.ok(homeworks);
    }

    @GetMapping("/{homeworkId}")
    public ResponseEntity<HomeworkDetail> getHomeworkDetail(@PathVariable Long homeworkId) {
        HomeworkDetail detail = homeworkService.getHomeworkDetail(homeworkId);
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/{homeworkId}/start")
    public ResponseEntity<?> startHomework(@PathVariable Long homeworkId,
                                                       @RequestParam Long studentId) {
        try {
            HomeworkPaper paper = homeworkService.startHomework(homeworkId, studentId);
            return ResponseEntity.ok(paper);
        } catch (RuntimeException e) {
            // 返回更详细的错误信息
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/{homeworkId}/submit")
    public ResponseEntity<Void> submitHomework(@PathVariable Long homeworkId,
                                               @RequestParam Long studentId,
                                               @RequestBody List<Answer> answers) {
        homeworkService.submitHomeworkAnswers(homeworkId, studentId, answers);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{homeworkId}/draft")
    public ResponseEntity<Void> saveHomeworkDraft(@PathVariable Long homeworkId,
                                                  @RequestParam Long studentId,
                                                  @RequestBody List<Answer> answers) {
        homeworkService.saveHomeworkDraft(homeworkId, studentId, answers);
        return ResponseEntity.ok().build();
    }
}