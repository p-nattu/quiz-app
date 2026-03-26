package com.example.quiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.quiz.service.CsvService;

/**
 * CSVコントローラ
 */
@Controller
@RequestMapping("/csv")
public class CsvController {

    /** CSV処理用Service */
    @Autowired
    private CsvService service;

    /**
     * CSVアップロード画面を表示
     */
    @GetMapping
    public String showUpload() {
        return "csv/upload";
    }

    /**
     * CSVファイルを受け取り、データベースに登録する
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, Model model) {

        try {
            // CSVを読み込み、登録件数を受け取る
            int count = service.importCsv(file);

            // 画面に表示するメッセージ
            model.addAttribute("msg", count + "件登録しました。");

        } catch (Exception e) {
            // エラー時のメッセージ
            model.addAttribute("msg", "エラーが発生しました: " + e.getMessage());
        }

        return "csv/result";
    }
    
    /**
     * 2択問題CSVアップロード画面を表示
     */
    @GetMapping("/quiz")
    public String showQuizUpload() {
        return "csv/upload-quiz";
    }

    /**
     * 2択問題CSVアップロード処理
     */
    @PostMapping("/quiz/upload")
    public String uploadQuizCsv(@RequestParam("file") MultipartFile file, Model model) {

        try {
            int count = service.importQuizCsv(file);
            model.addAttribute("msg", count + "件登録しました。");
        } catch (Exception e) {
            model.addAttribute("msg", "エラーが発生しました: " + e.getMessage());
        }

        return "csv/result";
    }
}