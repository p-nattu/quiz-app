package com.example.quiz.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * CSV読み込みに関するサービス
 */
public interface CsvService {
	// 4択問題CSVのインポート
    int importCsv(MultipartFile file) throws Exception;
    // 2択問題CSVのインポート
    int importQuizCsv(MultipartFile file) throws Exception;
}