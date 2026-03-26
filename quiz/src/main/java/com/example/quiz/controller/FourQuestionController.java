package com.example.quiz.controller;

import java.util.Optional;

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

import com.example.quiz.entity.FourQuestion;
import com.example.quiz.form.FourQuestionForm;
import com.example.quiz.service.FourQuestionService;

/** FourQuestionコントローラ */
@Controller
@RequestMapping("/four")
public class FourQuestionController {

    /** Serviceの注入 */
    @Autowired
    private FourQuestionService service;

    /**
     * フォーム初期化処理
     * 画面で使うオブジェクトを準備する
     */
    @ModelAttribute
    public FourQuestionForm setUpForm() {
    	return new FourQuestionForm();
    }

    /**
     * 一覧画面表示
     */
    @GetMapping("/list")
    public String list(Model model) {
        Iterable<FourQuestion> list = service.selectAll();

        // HTMLにデータ渡す
        model.addAttribute("list", list);

        return "four/list";
    }

    /**
     * 新規作成画面表示
     */
    @GetMapping("/create")
    public String create() {
        return "four/create";
    }

    /**
     * 登録処理
     */
    @PostMapping("/save")
    public String save(
            @Validated FourQuestionForm form,
            BindingResult bindingResult,
            Model model) {

        // 入力エラーがあれば戻す
        if (bindingResult.hasErrors()) {
            return "four/create";
        }

        // Form → Entity変換
        FourQuestion q = new FourQuestion();
        q.setQuestion(form.getQuestion());
        q.setChoice1(form.getChoice1());
        q.setChoice2(form.getChoice2());
        q.setChoice3(form.getChoice3());
        q.setChoice4(form.getChoice4());
        q.setAnswer(form.getAnswer());

        // DB登録
        service.insert(q);

        return "redirect:/four/list";
    }

    /**
     * 編集画面表示
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {

        Optional<FourQuestion> opt = service.selectOneById(id);

        if (opt.isPresent()) {
            FourQuestion q = opt.get();

            // Entity → Form変換
            FourQuestionForm form = new FourQuestionForm();
            form.setId(q.getId());
            form.setQuestion(q.getQuestion());
            form.setChoice1(q.getChoice1());
            form.setChoice2(q.getChoice2());
            form.setChoice3(q.getChoice3());
            form.setChoice4(q.getChoice4());
            form.setAnswer(q.getAnswer());

            model.addAttribute("fourQuestionForm", form);

            return "four/edit";
        }

        return "redirect:/four/list";
    }

    /**
     * 更新処理
     */
    @PostMapping("/update")
    public String update(
            @Validated FourQuestionForm form,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "four/edit";
        }

        FourQuestion q = new FourQuestion();
        q.setId(form.getId());
        q.setQuestion(form.getQuestion());
        q.setChoice1(form.getChoice1());
        q.setChoice2(form.getChoice2());
        q.setChoice3(form.getChoice3());
        q.setChoice4(form.getChoice4());
        q.setAnswer(form.getAnswer());

        service.update(q);

        return "redirect:/four/list";
    }

    /**
     * 削除処理
     */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteById(id);

        return "redirect:/four/list";
    }
    
    /**
     * 4択問題をランダムで1問表示
     */
    @GetMapping("/play")
    public String showPlay(Model model) {

        // ランダムで1問取得
        Optional<FourQuestion> opt = service.selectOneRandomQuestion();

        // 問題がない場合
        if (opt.isEmpty()) {
            model.addAttribute("msg", "4択問題がありません");
            return "four/answer";
        }

        // 取得した問題を画面に渡す
        model.addAttribute("question", opt.get());

        return "four/play";
    }

    /**
     * 正誤判定
     */
    @PostMapping("/check")
    public String check(
            @RequestParam Integer id,
            @RequestParam Integer answer,
            Model model) {

        // 正誤判定
        if (service.checkAnswer(id, answer)) {
            model.addAttribute("msg", "正解です！");
        } else {
            model.addAttribute("msg", "不正解です...");
        }

        return "four/answer";
    }
}