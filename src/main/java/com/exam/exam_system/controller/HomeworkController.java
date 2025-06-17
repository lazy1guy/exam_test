package com.exam.exam_system.controller;

import com.exam.exam_system.dto.Answer;
import com.exam.exam_system.dto.HomeworkDTO;
import com.exam.exam_system.dto.HomeworkPaper;
import com.exam.exam_system.dto.HomeworkDetail;
import com.exam.exam_system.service.HomeworkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<HomeworkPaper> startHomework(@PathVariable Long homeworkId,
                                                       @RequestParam Long studentId) {
        HomeworkPaper paper = homeworkService.startHomework(homeworkId, studentId);
        return ResponseEntity.ok(paper);
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