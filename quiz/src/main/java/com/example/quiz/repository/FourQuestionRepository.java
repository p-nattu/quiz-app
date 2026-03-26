package com.example.quiz.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.example.quiz.entity.FourQuestion;

/** FourQuestionテーブル：RepositoryImpl */
public interface FourQuestionRepository extends CrudRepository<FourQuestion, Integer> {
    @Query("SELECT id FROM four_questions ORDER BY RANDOM() LIMIT 1")
    Integer getRandomId();
}