package com.exam.exam_system.config;

import com.exam.exam_system.entity.*;
import com.exam.exam_system.repository.ExamRepository;
import com.exam.exam_system.repository.HomeworkRepository;
import com.exam.exam_system.repository.QuestionRepository;
import com.exam.exam_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final HomeworkRepository homeworkRepository;
    private final QuestionRepository questionRepository;

    public DataInitializer(UserRepository userRepository, ExamRepository examRepository,
                           HomeworkRepository homeworkRepository, QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.homeworkRepository = homeworkRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化用户数据
        if (userRepository.findByUsername("teacher1").isEmpty()) {
            User teacher1 = new User();
            teacher1.setUsername("teacher1");
            teacher1.setPassword("123456");
            teacher1.setFullName("张老师");
            teacher1.setRole("TEACHER");
            teacher1.setEmail("teacher1@example.com");
            userRepository.save(teacher1);

            User student1 = new User();
            student1.setUsername("student1");
            student1.setPassword("123456");
            student1.setFullName("李明");
            student1.setRole("STUDENT");
            student1.setEmail("student1@example.com");
            userRepository.save(student1);

            User student2 = new User();
            student2.setUsername("student2");
            student2.setPassword("123456");
            student2.setFullName("王芳");
            student2.setRole("STUDENT");
            student2.setEmail("student2@example.com");
            userRepository.save(student2);
        }

        // 初始化考试数据
        if (examRepository.findByTitle("数学考试").isEmpty()) {
            var teacher1 = userRepository.findByUsername("teacher1").orElseThrow();
            var mathExam = new Exam();
            mathExam.setTitle("数学考试");
            mathExam.setDescription("2023学年第一学期数学期中测试");
            mathExam.setSubject("数学");
            mathExam.setTeacher(teacher1);
            mathExam.setStartTime(LocalDateTime.of(2023, 10, 15, 9, 0));
            mathExam.setEndTime(LocalDateTime.of(2026, 10, 15, 11, 0));
            mathExam.setDuration(120);
            mathExam.setTotalScore(100);
            examRepository.save(mathExam);

            var englishExam = new Exam();
            englishExam.setTitle("英语期末考试");
            englishExam.setDescription("2023学年第一学期英语期末考试");
            englishExam.setSubject("英语");
            englishExam.setTeacher(teacher1);
            englishExam.setStartTime(LocalDateTime.of(2023, 12, 20, 9, 0));
            englishExam.setEndTime(LocalDateTime.of(2023, 12, 20, 11, 0));
            englishExam.setDuration(120);
            englishExam.setTotalScore(100);
            examRepository.save(englishExam);
        }

        // 初始化作业数据
        if (homeworkRepository.findByTitle("数学作业1").isEmpty()) {
            var teacher1 = userRepository.findByUsername("teacher1").orElseThrow();
            var mathHomework = new Homework();
            mathHomework.setTitle("数学作业1");
            mathHomework.setDescription("二次函数练习题");
            mathHomework.setSubject("数学");
            mathHomework.setTeacher(teacher1);
            mathHomework.setDeadline(LocalDateTime.of(2026, 10, 20, 23, 59));
            mathHomework.setTotalScore(100);
            homeworkRepository.save(mathHomework);

            var englishHomework = new Homework();
            englishHomework.setTitle("英语作业1");
            englishHomework.setDescription("阅读理解练习");
            englishHomework.setSubject("英语");
            englishHomework.setTeacher(teacher1);
            englishHomework.setDeadline(LocalDateTime.of(2023, 10, 10, 23, 59));
            englishHomework.setTotalScore(50);
            homeworkRepository.save(englishHomework);
        }

        // 初始化题目数据
        var mathExam = examRepository.findByTitle("数学考试").orElseThrow();
        if (questionRepository.findByContent("2+4=?").isEmpty()) {
            var question1 = new Question();
            question1.setContent("2+4=?");
            question1.setType("SINGLE_CHOICE");
            question1.setOptions("[\"3\", \"6\", \"5\"]");
            question1.setAnswer("6");
            question1.setScore(5);
            question1.setSubject("数学");
            question1.setExam(mathExam);
            questionRepository.save(question1);
        }

        if(questionRepository.findByContent("解方程: x^2 - 4 = 0").isEmpty()){
            var question2 = new Question();
            question2.setContent("解方程: x^2 - 4 = 0");
            question2.setType("SHORT_ANSWER");
            question2.setOptions(null);
            question2.setAnswer("x=2或x=-2");
            question2.setScore(10);
            question2.setSubject("数学");
            question2.setExam(mathExam);
            questionRepository.save(question2);
        }
    }
}