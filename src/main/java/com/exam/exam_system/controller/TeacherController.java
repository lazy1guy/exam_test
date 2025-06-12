// 教师控制器
package com.exam.exam_system.controller;

import com.exam.exam_system.dto.*;
import com.exam.exam_system.entity.*;
import com.exam.exam_system.service.TeacherService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

    // 导出考试结果为 Excel 文件
    @GetMapping("/exams/{examId}/export")
    public ResponseEntity<InputStreamResource> exportExamResults(@PathVariable Long examId) {
        byte[] excelData = teacherService.exportExamResults(examId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam_results.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(new java.io.ByteArrayInputStream(excelData)));
    }

    // 导出作业结果为 Excel 文件
    @GetMapping("/homeworks/{homeworkId}/export")
    public ResponseEntity<InputStreamResource> exportHomeworkResults(@PathVariable Long homeworkId) {
        byte[] excelData = teacherService.exportHomeworkResults(homeworkId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=homework_results.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(new java.io.ByteArrayInputStream(excelData)));
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

}
