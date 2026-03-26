package com.example.quiz.controller;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.quiz.entity.Quiz;
import com.example.quiz.form.QuizForm;
import com.example.quiz.service.QuizService;


/** Quizコントローラ */
@Controller
@RequestMapping("/quiz")
public class QuizController {
    /** DI対象 */
    @Autowired
    QuizService service;
    /** 「form-backing bean」の初期化 */
    @ModelAttribute
    public QuizForm setUpForm() {
        QuizForm form = new QuizForm();
        // ラジオボタンのデフォルト値設定
        form.setAnswer(true);
        return form;
    }
    /** Quizの一覧を表示します */
    @GetMapping
    public String showList(QuizForm quizForm, Model model) {
        // 新規登録設定
        quizForm.setNewQuiz(true);
        // 掲示板の一覧を取得する
        Iterable<Quiz> list = service.selectAll();
        // 表示用「Model」への格納
        model.addAttribute("list", list);
        model.addAttribute("title", "登録用フォーム");
        return "crud";
    }
    
    /** Quizデータを1件挿入 */
    @PostMapping("/insert")
    public String insert(@Validated QuizForm quizForm, BindingResult bindingResult,
                         Model model, RedirectAttributes redirectAttributes) {
        // FormからEntityへの詰め替え
        Quiz quiz = new Quiz();
        quiz.setQuestion(quizForm.getQuestion());
        quiz.setAnswer(quizForm.getAnswer());
        quiz.setAuthor(quizForm.getAuthor());
        // 入力チェック
        if (!bindingResult.hasErrors()) {
            service.insertQuiz(quiz);
            redirectAttributes.addFlashAttribute("complete", "登録が完了しました");
            return "redirect:/quiz";
        } else {
            // エラーがある場合は一覧表示処理を呼びます
            return showList(quizForm, model);
        }
    }
    
    /** Quizデータを1件取得し、フォームに表示する */
    @GetMapping("/{id}")
    public String showUpdate(QuizForm quizForm,
                             @PathVariable Integer id,
                             Model model) {

        // Quizを取得(Optionalでラップ)
        Optional<Quiz> quizOpt = service.selectOneById(id);

        // QuizFormへの詰め直し
        Optional<QuizForm> quizFormOpt = quizOpt.map(t -> makeQuizForm(t));

        // QuizFormがnullでなければ中身を取り出す
        if (quizFormOpt.isPresent()) {
            quizForm = quizFormOpt.get();
        }

        // 更新用のModelを作成
        makeUpdateModel(quizForm, model);

        return "crud";
    }
    /** 更新用のModelを作成 */
    private void makeUpdateModel(QuizForm quizForm, Model model) {

        model.addAttribute("id", quizForm.getId());
        quizForm.setNewQuiz(false);
        model.addAttribute("quizForm", quizForm);
        model.addAttribute("title", "更新用フォーム");
    }
    @PostMapping("/update")
    public String update(@Validated QuizForm quizForm,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        // FormからEntityへ詰め替え
        Quiz quiz = makeQuiz(quizForm);

        if (!result.hasErrors()) {

            // 更新処理
            service.updateQuiz(quiz);

            // フラッシュスコープ
            redirectAttributes.addFlashAttribute("complete", "更新が完了しました");

            // 一覧へリダイレクト
            return "redirect:/quiz/" + quiz.getId();

        } else {

            // エラー時
            makeUpdateModel(quizForm, model);
            return "crud";
        }
    }
    // ---------- 【以下はFromとDomainObjectの詰め直し ----------】
    /** QuizFormからQuizに詰め直して返り値と返します */
    private Quiz makeQuiz(QuizForm quizForm) {
        Quiz quiz = new Quiz();
        quiz.setId(quizForm.getId());
        quiz.setQuestion(quizForm.getQuestion());
        quiz.setAnswer(quizForm.getAnswer());
        quiz.setAuthor(quizForm.getAuthor());
        return quiz;
    }
    /** QuizからQuizFormに詰め直して返り値と返します */
    private QuizForm makeQuizForm(Quiz quiz) {
        QuizForm form = new QuizForm();
        form.setId(quiz.getId());
        form.setQuestion(quiz.getQuestion());
        form.setAnswer(quiz.getAnswer());
        form.setAuthor(quiz.getAuthor());
        form.setNewQuiz(false);
        return form;
    }
    
    /** idをKeyにしてデータを削除する */
    @PostMapping("/delete")
    public String delete(
            @RequestParam("id") String id,
            Model model,
            RedirectAttributes redirectAttributes) {
        // タスクを1件削除してリダイレクト
        service.deleteQuizById(Integer.parseInt(id));
        redirectAttributes.addFlashAttribute("delcomplete", "削除が完了しました");
        return "redirect:/quiz";
    }
    
 // クイズをランダムで表示
    @GetMapping("/play")
    public String showQuiz(QuizForm quizForm, Model model) {

        Optional<Quiz> quizOpt = service.selectOneRandomQuiz();

        if (quizOpt.isPresent()) {
            Optional<QuizForm> quizFormOpt = quizOpt.map(t -> makeQuizForm(t));
            quizForm = quizFormOpt.get();
        } else {
            model.addAttribute("msg", "問題がありません・・・");
            return "play";
        }

        model.addAttribute("quizForm", quizForm);
        return "play";
    }
    @PostMapping("/check")
    public String checkQuiz(QuizForm quizForm,
                            @RequestParam Boolean answer,
                            Model model) {

        if (service.checkQuiz(quizForm.getId(), answer)) {
            model.addAttribute("msg", "正解です！");
        } else {
            model.addAttribute("msg", "残念、不正解です・・・");
        }

        return "answer";
    }
    
    /**
     * 10問クイズ開始処理
     */
    @GetMapping("/play10")
    public String startPlay10(Model model, HttpSession session) {

        // ランダムで問題取得
        List<Quiz> quizList = service.selectRandomQuizList();

        // ❗ 10問未満チェック
        if (quizList.size() < 10) {
            model.addAttribute("msg", "10問ありません。問題を追加してください。");
            return "result10";
        }

        // セッションに保存
        session.setAttribute("quizList", quizList);
        session.setAttribute("currentIndex", 0);
        session.setAttribute("score", 0);

        // 最初の1問
        Quiz quiz = quizList.get(0);

        model.addAttribute("quiz", quiz);
        model.addAttribute("currentNumber", 1);
        model.addAttribute("totalCount", 10);

        return "play10";
    }
    
    /**
     * 10問クイズの回答処理
     */
    @PostMapping("/play10")
    public String answerPlay10(
            @RequestParam Integer id,
            @RequestParam Boolean answer,
            Model model,
            HttpSession session) {

        // セッションから取得
        List<Quiz> quizList = (List<Quiz>) session.getAttribute("quizList");
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");
        Integer score = (Integer) session.getAttribute("score");

        // セッション切れ対策
        if (quizList == null || currentIndex == null || score == null) {
            model.addAttribute("msg", "セッションが切れました。もう一度最初からやり直してください。");
            return "result10";
        }

        // 正解判定
        if (service.checkQuiz(id, answer)) {
            score++;
        }

        // 次の問題へ進める
        currentIndex++;

        // 全問終了なら結果画面へ
        if (currentIndex >= quizList.size()) {
            model.addAttribute("score", score);
            model.addAttribute("totalCount", quizList.size());

            // セッション削除
            session.removeAttribute("quizList");
            session.removeAttribute("currentIndex");
            session.removeAttribute("score");

            return "result10";
        }

        // セッション更新
        session.setAttribute("currentIndex", currentIndex);
        session.setAttribute("score", score);

        // 次の問題を表示
        Quiz nextQuiz = quizList.get(currentIndex);
        model.addAttribute("quiz", nextQuiz);
        model.addAttribute("currentNumber", currentIndex + 1);
        model.addAttribute("totalCount", quizList.size());

        return "play10";
    }
}