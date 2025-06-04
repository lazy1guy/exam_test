// 错题本控制器
package com.exam.exam_system.controller;

import com.exam.exam_system.dto.ErrorQuestion;
import com.exam.exam_system.dto.PracticePaper;
import com.exam.exam_system.service.ErrorBookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/error-book")
public class ErrorBookController {

    private final ErrorBookService errorBookService;

    public ErrorBookController(ErrorBookService errorBookService) {
        this.errorBookService = errorBookService;
    }

    @GetMapping
    public ResponseEntity<List<ErrorQuestion>> getErrorBook(@RequestParam Long userId) {
        List<ErrorQuestion> errors = errorBookService.getErrorBook(userId);
        return ResponseEntity.ok(errors);
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<List<ErrorQuestion>> getErrorBookBySubject(@RequestParam Long userId,
                                                                     @PathVariable String subject) {
        List<ErrorQuestion> errors = errorBookService.getErrorBookBySubject(userId, subject);
        return ResponseEntity.ok(errors);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> removeFromErrorBook(@PathVariable Long questionId,
                                                    @RequestParam Long userId) {
        errorBookService.removeFromErrorBook(questionId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{questionId}/note")
    public ResponseEntity<Void> addErrorNote(@PathVariable Long questionId,
                                             @RequestParam Long userId,
                                             @RequestBody String note) {
        errorBookService.addErrorNote(questionId, userId, note);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/practice")
    public ResponseEntity<PracticePaper> practiceErrorQuestions(@RequestParam Long userId,
                                                                @RequestParam(defaultValue = "10") int count) {
        PracticePaper paper = errorBookService.generatePracticePaper(userId, count);
        return ResponseEntity.ok(paper);
    }
}
