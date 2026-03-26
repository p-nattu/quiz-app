package com.example.quiz.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.quiz.entity.FourQuestion;
import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.FourQuestionRepository;
import com.example.quiz.repository.QuizRepository;

/**
 * CSV読み込み処理の実装クラス
 */
@Service
public class CsvServiceImpl implements CsvService {

    /** 4択問題保存用Repository */
    @Autowired
    private FourQuestionRepository repository;
    
    /** 2択問題保存用Repository */
    @Autowired
    private QuizRepository quizRepository;

    /**
     * CSVを1行ずつ読み込み、4択問題としてDBに登録する
     */
    @Override
    public int importCsv(MultipartFile file) throws Exception {

        // 登録件数を数える変数
        int count = 0;

        // アップロードファイルを読み込むための準備
        BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()));

        String line;

        // 1行ずつ読み込む
        while ((line = br.readLine()) != null) {

            // ヘッダー行はスキップ
            if (line.startsWith("question")) {
                continue;
            }

            // カンマ区切りで分割
            String[] data = line.split(",");

            // CSVの列数が足りない場合はエラーにする
            if (data.length != 6) {
                throw new IllegalArgumentException("CSVの列数が正しくありません。");
            }

            // CSVの1行をEntityに詰め替える
            FourQuestion q = new FourQuestion();
            q.setQuestion(data[0]);
            q.setChoice1(data[1]);
            q.setChoice2(data[2]);
            q.setChoice3(data[3]);
            q.setChoice4(data[4]);
            q.setAnswer(Integer.parseInt(data[5]));

            // DBへ保存
            repository.save(q);

            // 件数を加算
            count++;
        }

        return count;
    }
    
    /**
     * CSVを1行ずつ読み込み、2択問題としてDBに登録する
     */
    @Override
    public int importQuizCsv(MultipartFile file) throws Exception {

        int count = 0;

        BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()));

        String line;

        while ((line = br.readLine()) != null) {

            // 空行はスキップ
            if (line.trim().isEmpty()) {
                continue;
            }

            // ヘッダー行はスキップ
            if (line.startsWith("question")) {
                continue;
            }

            String[] data = line.split(",");

            // question,answer,author の3列を想定
            if (data.length != 3) {
                throw new IllegalArgumentException("2択CSVの列数が正しくありません。");
            }

            Quiz quiz = new Quiz();
            quiz.setQuestion(data[0]);
            quiz.setAnswer(Boolean.parseBoolean(data[1]));
            quiz.setAuthor(data[2]);

            quizRepository.save(quiz);
            count++;
        }

        return count;
    }
}