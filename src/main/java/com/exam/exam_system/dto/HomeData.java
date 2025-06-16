package com.exam.exam_system.dto;

import com.exam.exam_system.entity.Exam;
import com.exam.exam_system.entity.Homework;
import com.exam.exam_system.entity.Notification;
import com.exam.exam_system.entity.Score;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeData implements Serializable
{
    private static final long serialVersionUID = 1L;

    private List<Homework> recentHomeworks; // 最近的作业列表
    private List<Exam> recentExams; // 最近的考试列表
    private List<Score> latestScores; // 最新的成绩列表
}