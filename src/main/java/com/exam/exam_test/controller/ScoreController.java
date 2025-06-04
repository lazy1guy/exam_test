// 成绩控制器
package com.exam.exam_test.controller;

import com.exam.exam_test.dto.*;
import com.exam.exam_test.service.ScoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {
    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @GetMapping
    public ResponseEntity<ScoreSummary> getScoreSummary(@RequestParam Long userId) {
        ScoreSummary summary = scoreService.getScoreSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/exams")
    public ResponseEntity<List<ExamScore>> getExamScores(@RequestParam Long userId) {
        List<ExamScore> scores = scoreService.getExamScores(userId);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/homeworks")
    public ResponseEntity<List<HomeworkScore>> getHomeworkScores(@RequestParam Long userId) {
        List<HomeworkScore> scores = scoreService.getHomeworkScores(userId);
        return ResponseEntity.ok(scores);
    }

    @GetMapping("/exams/{examId}")
    public ResponseEntity<ExamScoreDetail> getExamScoreDetail(@PathVariable Long examId,
                                                              @RequestParam Long userId) {
        ExamScoreDetail detail = scoreService.getExamScoreDetail(examId, userId);
        return ResponseEntity.ok(detail);
    }

    @GetMapping("/homeworks/{homeworkId}")
    public ResponseEntity<HomeworkScoreDetail> getHomeworkScoreDetail(@PathVariable Long homeworkId,
                                                                      @RequestParam Long userId) {
        HomeworkScoreDetail detail = scoreService.getHomeworkScoreDetail(homeworkId, userId);
        return ResponseEntity.ok(detail);
    }
}