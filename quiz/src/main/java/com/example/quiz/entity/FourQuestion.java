package com.example.quiz.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

/** four_questionsテーブル用：Entity */
@Data
@Table("four_questions")
public class FourQuestion {

    @Id
    private Integer id;

    private String question;
    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;
    private Integer answer;
}