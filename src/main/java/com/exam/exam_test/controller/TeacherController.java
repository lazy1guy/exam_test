// 教师控制器
package com.exam.exam_test.controller;

import com.exam.exam_test.entity.*;
import com.exam.exam_test.service.TeacherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping("/exams")
    public ResponseEntity<Exam> createExam(@RequestBody ExamCreateRequest request) {
        Exam exam = teacherService.createExam(request);
        return ResponseEntity.ok(exam);
    }

    @PostMapping("/homeworks")
    public ResponseEntity<Homework> createHomework(@RequestBody HomeworkCreateRequest request) {
        Homework homework = teacherService.createHomework(request);
        return ResponseEntity.ok(homework);
    }

    @GetMapping("/exams/{examId}/results")
    public ResponseEntity<ExamResults> getExamResults(@PathVariable Long examId) {
        ExamResults results = teacherService.getExamResults(examId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/homeworks/{homeworkId}/results")
    public ResponseEntity<HomeworkResults> getHomeworkResults(@PathVariable Long homeworkId) {
        HomeworkResults results = teacherService.getHomeworkResults(homeworkId);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/homeworks/{homeworkId}/grade")
    public ResponseEntity<Void> gradeHomework(@PathVariable Long homeworkId,
                                              @RequestParam Long studentId,
                                              @RequestBody List<SubjectiveGrading> gradings) {
        teacherService.gradeHomework(homeworkId, studentId, gradings);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/exams/{examId}/publish")
    public ResponseEntity<Void> publishExamResults(@PathVariable Long examId) {
        teacherService.publishExamResults(examId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/classes/{classId}/analysis")
    public ResponseEntity<ClassAnalysis> getClassAnalysis(@PathVariable Long classId) {
        ClassAnalysis analysis = teacherService.getClassAnalysis(classId);
        return ResponseEntity.ok(analysis);
    }
}
