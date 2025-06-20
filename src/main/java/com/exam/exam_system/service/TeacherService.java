// 教师服务
package com.exam.exam_system.service;

import com.exam.exam_system.dto.*;
import com.exam.exam_system.entity.*;
import com.exam.exam_system.repository.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeacherService {
    private static final Logger log = LoggerFactory.getLogger(ExamService.class);

    private final ExamRepository examRepository;
    private final HomeworkRepository homeworkRepository;
    private final QuestionRepository questionRepository;
    private final ScoreRepository scoreRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final UserRepository userRepository;

    public TeacherService(ExamRepository examRepository, HomeworkRepository homeworkRepository,
                          QuestionRepository questionRepository, ScoreRepository scoreRepository,
                          AnswerRecordRepository answerRecordRepository, UserRepository userRepository) {
        this.examRepository = examRepository;
        this.homeworkRepository = homeworkRepository;
        this.questionRepository = questionRepository;
        this.scoreRepository = scoreRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @CacheEvict(value = {"examListCache","examDetailCache", "homeDataCache"}, allEntries = true)
    public ExamDTO createExam(ExamCreateRequest request) {
        Exam exam = new Exam();
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setSubject(request.getSubject());
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("未找到指定的教师"));
        exam.setTeacher(teacher);
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setDuration(request.getDuration());

        // 计算总分
        int totalScore = request.getQuestions().stream()
                .mapToInt(QuestionDTO::getScore)
                .sum();
        exam.setTotalScore(totalScore);
        Exam savedExam = examRepository.save(exam);
        // 保存题目
        List<Question> questionEntities = new ArrayList<>();
        for (QuestionDTO q : request.getQuestions()) {
            Question entity = new Question();
            entity.setContent(q.getContent());
            entity.setOptions(q.getOptions());
            entity.setAnswer(q.getAnswer());
            entity.setScore(q.getScore());
            entity.setType(q.getType());
            entity.setExam(savedExam);
            entity.setSubject(q.getSubject());
            questionEntities.add(questionRepository.save(entity));
        }
        savedExam.setQuestions(questionEntities);
        savedExam = examRepository.save(savedExam);
        return new ExamDTO(savedExam);
    }

    @Transactional
    @CacheEvict(value = {"homeworkListCache","homeworkDetailCache", "homeDataCache"}, allEntries = true)
    public HomeworkDTO createHomework(HomeworkCreateRequest request) {
        Homework homework = new Homework();
        homework.setTitle(request.getTitle());
        homework.setDescription(request.getDescription());
        homework.setSubject(request.getSubject());
        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new IllegalArgumentException("未找到指定的教师"));
        homework.setTeacher(teacher);
        homework.setDeadline(request.getDeadline());

        // 计算总分
        int totalScore = request.getQuestions().stream()
                .mapToInt(QuestionDTO::getScore)
                .sum();
        homework.setTotalScore(totalScore);
        Homework savedHomework = homeworkRepository.save(homework);
        // 保存题目
        List<Question> questionEntities = new ArrayList<>();
        for (QuestionDTO q : request.getQuestions()) {
            Question entity = new Question();
            entity.setContent(q.getContent());
            entity.setOptions(q.getOptions());
            entity.setAnswer(q.getAnswer());
            entity.setScore(q.getScore());
            entity.setType(q.getType());
            entity.setHomework(savedHomework);
            entity.setSubject(q.getSubject());
            questionEntities.add(questionRepository.save(entity));
        }
        savedHomework.setQuestions(questionEntities);
        savedHomework = homeworkRepository.save(savedHomework);
        return new HomeworkDTO(savedHomework);
    }

    public ExamResults getExamResults(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));

        ExamDTO examDTO = new ExamDTO(exam);

        List<Score> scores = scoreRepository.findByExamId(examId);

        ExamResults results = new ExamResults();
        results.setExam(examDTO);

        // 计算统计信息
        double avgScore = calculateAverageScore(scores);
        double passRate = calculatePassRate(scores);
        int maxScore = calculateMaxScore(scores);
        int minScore = calculateMinScore(scores);
        Map<Long, Double> questionAvgScores = calculateQuestionAvgScores(scores, exam.getQuestions(), examId, "exam");
        Map<String, Long> scoreDistribution = calculateScoreDistribution(scores);

        results.setAvgScore(avgScore);
        results.setPassRate(passRate);
        results.setMaxScore(maxScore);
        results.setMinScore(minScore);
        results.setQuestionAvgScores(questionAvgScores);
        results.setScoreDistribution(scoreDistribution);

        // 学生成绩列表
        List<StudentScore> studentScores = scores.stream()
                .map(s -> {
                    StudentScore ss = new StudentScore();
                    ss.setStudentId(s.getStudent().getId());
                    ss.setStudentName(s.getStudent().getUsername());
                    ss.setScore(s.getScore());
                    ss.setTotalScore(s.getTotalScore());
                    return ss;
                })
                .collect(Collectors.toList());
        results.setStudentScores(studentScores);

        return results;
    }

    public HomeworkResults getHomeworkResults(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        HomeworkDTO homeworkDTO = new HomeworkDTO(homework);

        List<Score> scores = scoreRepository.findByHomeworkId(homeworkId);

        HomeworkResults results = new HomeworkResults();
        results.setHomework(homeworkDTO);

        // 计算统计信息
        double avgScore = calculateAverageScore(scores);
        log.info("计算平均分");
        double completionRate = calculateCompletionRate(scores);
        log.info("计算完成率");
        int maxScore = calculateMaxScore(scores);
        log.info("计算最高分");
        int minScore = calculateMinScore(scores);
        log.info("计算最低分");
        Map<Long, Double> questionAvgScores = calculateQuestionAvgScores(scores, homework.getQuestions(), homeworkId, "homework");
        log.info("计算每题得分");
        Map<String, Long> scoreDistribution = calculateScoreDistribution(scores);
        log.info("计算分数分布");

        results.setAvgScore(avgScore);
        results.setCompletionRate(completionRate);
        results.setMaxScore(maxScore);
        results.setMinScore(minScore);
        results.setQuestionAvgScores(questionAvgScores);
        results.setScoreDistribution(scoreDistribution);

        // 学生成绩列表
        List<StudentScore> studentScores = scores.stream()
                .map(s -> {
                    StudentScore ss = new StudentScore();
                    ss.setStudentId(s.getStudent().getId());
                    ss.setStudentName(s.getStudent().getUsername());
                    ss.setScore(s.getScore());
                    ss.setTotalScore(s.getTotalScore());
                    ss.setStatus(s.getStatus());
                    return ss;
                })
                .collect(Collectors.toList());
        results.setStudentScores(studentScores);

        return results;
    }

    // 导出考试结果为 Excel 文件
    public byte[] exportExamResults(Long examId) {
        ExamResults results = getExamResults(examId);
        return generateExcel(results);
    }

    // 导出作业结果为 Excel 文件
    public byte[] exportHomeworkResults(Long homeworkId) {
        HomeworkResults results = getHomeworkResults(homeworkId);
        return generateExcel(results);
    }

    private double calculateAverageScore(List<Score> scores) {
        return scores.stream()
                .mapToInt(Score::getScore)
                .average()
                .orElse(0.0);
    }

    private double calculatePassRate(List<Score> scores) {
        long passedCount = scores.stream()
                .filter(s -> "PASSED".equals(s.getStatus()))
                .count();
        return scores.isEmpty() ? 0.0 : (double) passedCount / scores.size();
    }

    private double calculateCompletionRate(List<Score> scores) {
        long completedCount = scores.stream()
                .filter(s -> !"LATE".equals(s.getStatus()))
                .count();
        return scores.isEmpty() ? 0.0 : (double) completedCount / scores.size();
    }

    private int calculateMaxScore(List<Score> scores) {
        return scores.stream()
                .mapToInt(Score::getScore)
                .max()
                .orElse(0);
    }

    private int calculateMinScore(List<Score> scores) {
        return scores.stream()
                .mapToInt(Score::getScore)
                .min()
                .orElse(0);
    }

    private Map<Long, Double> calculateQuestionAvgScores(List<Score> scores, List<Question> questions, Long examOrHomeworkId, String type) {
        // 获取所有学生的答题记录
        List<AnswerRecord> answerRecords;
        if ("exam".equals(type)) {
            answerRecords = answerRecordRepository.findByExamId(examOrHomeworkId);
        } else {
            answerRecords = answerRecordRepository.findByHomeworkIdAndIsDraftFalse(examOrHomeworkId);
        }

        // 构建问题 ID 和平均分的映射
        Map<Long, Double> questionAvgScores = new HashMap<>();

        for (Question question : questions) {
            long questionId = question.getId();

            // 过滤出与当前问题相关的答题记录，并计算平均分
            double avgScore = answerRecords.stream()
                    .filter(record -> record.getQuestion().getId().equals(questionId))
                    .mapToInt(AnswerRecord::getScore)
                    .average()
                    .orElse(0.0);

            // 将问题 ID 和平均分存入映射
            questionAvgScores.put(questionId, avgScore);
        }

        return questionAvgScores;

    }


    private Map<String, Long> calculateScoreDistribution(List<Score> scores) {
        Map<String, Long> distribution = new HashMap<>();
        distribution.put("0-59", 0L);
        distribution.put("60-69", 0L);
        distribution.put("70-79", 0L);
        distribution.put("80-89", 0L);
        distribution.put("90-100", 0L);

        for (Score score : scores) {
            int scoreValue = score.getScore();
            if (scoreValue >= 0 && scoreValue <= 59) {
                distribution.put("0-59", distribution.get("0-59") + 1);
            } else if (scoreValue >= 60 && scoreValue <= 69) {
                distribution.put("60-69", distribution.get("60-69") + 1);
            } else if (scoreValue >= 70 && scoreValue <= 79) {
                distribution.put("70-79", distribution.get("70-79") + 1);
            } else if (scoreValue >= 80 && scoreValue <= 89) {
                distribution.put("80-89", distribution.get("80-89") + 1);
            } else if (scoreValue >= 90 && scoreValue <= 100) {
                distribution.put("90-100", distribution.get("90-100") + 1);
            }
        }
        return distribution;
    }

    private byte[] generateExcel(Object results) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Results");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学生ID", "学生姓名", "得分", "总分"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            if (results instanceof ExamResults examResults) {
                int rowNum = 1;
                for (StudentScore score : examResults.getStudentScores()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(score.getStudentId());
                    row.createCell(1).setCellValue(score.getStudentName());
                    row.createCell(2).setCellValue(score.getScore());
                    row.createCell(3).setCellValue(score.getTotalScore());
                }
            } else if (results instanceof HomeworkResults homeworkResults) {
                int rowNum = 1;
                for (StudentScore score : homeworkResults.getStudentScores()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(score.getStudentId());
                    row.createCell(1).setCellValue(score.getStudentName());
                    row.createCell(2).setCellValue(score.getScore());
                    row.createCell(3).setCellValue(score.getTotalScore());
                }
            }

            // 写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出失败", e);
        }
    }


//    @Transactional
//    public void gradeHomework(Long homeworkId, Long studentId, List<SubjectiveGrading> gradings) {
//        for (SubjectiveGrading grading : gradings) {
//            // 更新答题记录
//            List<AnswerRecord> records = answerRecordRepository.findByStudentIdAndQuestionId(studentId, grading.getQuestionId());
//            if (!records.isEmpty()) {
//                AnswerRecord record = records.get(0);
//                record.setScore(grading.getScore());
//                record.setIsCorrect(grading.getScore() > 0);
//                answerRecordRepository.save(record);
//            }
//        }
//
//        // 重新计算总分
//        List<AnswerRecord> records = answerRecordRepository.findByStudentIdAndHomeworkId(studentId, homeworkId);
//        int totalScore = records.stream()
//                .filter(r -> r.getScore() != null)
//                .mapToInt(AnswerRecord::getScore)
//                .sum();
//
//        // 更新成绩
//        scoreRepository.findByStudentAndHomework(studentId, homeworkId)
//                .ifPresent(score -> {
//                    score.setScore(totalScore);
//                    score.setStatus("COMPLETED");
//                    scoreRepository.save(score);
//                });
//    }

    public void publishExamResults(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));
        exam.setPublished(true);
        examRepository.save(exam);
    }

    @Transactional
    @CacheEvict(value = {"examListCache","examDetailCache", "homeDataCache"}, allEntries = true)
    public void deleteExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("考试不存在"));

        // 1. 先删除所有相关的答题记录
        answerRecordRepository.deleteByExamId(examId);
        log.info("删除答题记录");

        // 2. 删除所有相关问题
        questionRepository.deleteByExamId(examId);
        log.info("删除试题");

        // 3. 删除所有相关成绩
        scoreRepository.deleteByExamId(examId);
        log.info("删除成绩");

        // 4. 最后删除考试本身
        examRepository.delete(exam);
        log.info("删除考试");
    }

    @Transactional
    @CacheEvict(value = {"homeworkListCache","homeworkDetailCache", "homeDataCache"}, allEntries = true)
    public void deleteHomework(Long homeworkId) {
        Homework homework = homeworkRepository.findById(homeworkId)
                .orElseThrow(() -> new RuntimeException("作业不存在"));

        // 1. 先删除所有相关的答题记录
        answerRecordRepository.deleteByHomeworkId(homeworkId);
        log.info("删除答题记录");

        // 2. 删除所有相关问题
        questionRepository.deleteByHomeworkId(homeworkId);
        log.info("删除试题");

        // 3. 删除所有相关成绩
        scoreRepository.deleteByHomeworkId(homeworkId);
        log.info("删除成绩");

        // 4. 最后删除作业本身
        homeworkRepository.delete(homework);
        log.info("删除作业");
    }
}
