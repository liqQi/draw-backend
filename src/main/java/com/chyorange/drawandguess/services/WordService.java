package com.chyorange.drawandguess.services;

import com.chyorange.drawandguess.models.Question;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordService {

    public List<Question> questions = new ArrayList<>();

    private volatile static WordService instance;

    public static WordService getInstance() {
        if (instance == null) {
            synchronized (WordService.class) {
                if (instance == null) {
                    instance = new WordService();
                    instance.init();
                }
            }
        }
        return instance;
    }

    public void init() {
        try {
            String out = WordService.class.getResource("out.txt").getFile();
            FileReader reader = new FileReader(out);
            BufferedReader br = new BufferedReader(reader);
            String s;
            while ((s = br.readLine()) != null) {
                if (s.isEmpty()) {
                    continue;
                }
                String[] split = s.split(" ");
                Question question = new Question();
                question.setQuestion(split[0]);
                question.setHintType(split[1]);
                question.setHintCount(split[2]);
                questions.add(question);
            }
            br.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Random random = new Random();

    public Question getQuestion() {
        return questions.get(random.nextInt(questions.size()));
    }
}
