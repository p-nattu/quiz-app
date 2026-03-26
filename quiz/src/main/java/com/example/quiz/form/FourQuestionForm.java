package com.example.quiz.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/** Form */
@Data
public class FourQuestionForm {

    /** ID（更新時に使用） */
    private Integer id;

    /** 問題文 */
    @NotBlank
    private String question;

    /** 選択肢1 */
    @NotBlank
    private String choice1;

    /** 選択肢2 */
    @NotBlank
    private String choice2;

    /** 選択肢3 */
    @NotBlank
    private String choice3;

    /** 選択肢4 */
    @NotBlank
    private String choice4;

    /** 正解番号（1〜4） */
    @NotNull
    private Integer answer;
}