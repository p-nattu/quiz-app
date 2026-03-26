package com.example.quiz.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.quiz.entity.FourQuestion;
import com.example.quiz.repository.FourQuestionRepository;

@Service
public class FourQuestionServiceImpl implements FourQuestionService {

    /** Repositoryの注入 */
    @Autowired
    private FourQuestionRepository repository;

    /** 全件取得 */
    @Override
    public Iterable<FourQuestion> selectAll() {
        return repository.findAll();
    }

    /** 1件取得 */
    @Override
    public Optional<FourQuestion> selectOneById(Integer id) {
        return repository.findById(id);
    }

    /** ランダムで1件取得 */
    @Override
    public Optional<FourQuestion> selectOneRandomQuestion() {
        // ランダムでidを1件取得
        Integer randId = repository.getRandomId();

        // 問題が存在しない場合
        if (randId == null) {
            return Optional.empty();
        }

        // 取得したidで問題を1件取得
        return repository.findById(randId);
    }

    /** 正誤判定 */
    @Override
    public boolean checkAnswer(Integer id, Integer answer) {
        boolean check = false;

        // 対象の4択問題を取得
        Optional<FourQuestion> opt = repository.findById(id);

        if (opt.isPresent()) {
            FourQuestion q = opt.get();

            // 正解番号と回答番号を比較
            if (q.getAnswer().equals(answer)) {
                check = true;
            }
        }

        return check;
    }

    /** 登録処理 */
    @Override
    public void insert(FourQuestion fourQuestion) {
        repository.save(fourQuestion);
    }

    /** 更新処理 */
    @Override
    public void update(FourQuestion fourQuestion) {
        repository.save(fourQuestion);
    }

    /** 削除処理 */
    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}