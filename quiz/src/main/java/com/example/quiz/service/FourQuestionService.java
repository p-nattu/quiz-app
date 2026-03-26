package com.example.quiz.service;

import java.util.Optional;

import com.example.quiz.entity.FourQuestion;

/**
 * 4択問題に関するサービス処理：Service
 */
public interface FourQuestionService {

    /** 4択問題を全件取得します */
    Iterable<FourQuestion> selectAll();

    /** 指定IDの4択問題を1件取得します */
    Optional<FourQuestion> selectOneById(Integer id);

    /** ランダムで4択問題を1件取得します */
    Optional<FourQuestion> selectOneRandomQuestion();

    /** 指定IDの4択問題と回答番号を照合して正誤判定します */
    boolean checkAnswer(Integer id, Integer answer);

    /** 4択問題を新規登録します */
    void insert(FourQuestion fourQuestion);

    /** 4択問題を更新します */
    void update(FourQuestion fourQuestion);

    /** 指定IDの4択問題を削除します */
    void deleteById(Integer id);
}