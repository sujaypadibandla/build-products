package com.telusko.quizapp.service;

import com.telusko.quizapp.dao.QuestionRepository;
import com.telusko.quizapp.dao.QuizRepository;
import com.telusko.quizapp.model.Question;
import com.telusko.quizapp.model.QuestionWrapper;
import com.telusko.quizapp.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    QuizRepository quizRepository;

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByCategory( String category) {
        return questionRepository.findByCategory(category);
    }

    public String addQuestion(Question question) {
        Question out = questionRepository.save(question);
        // Directly returning a success message or null check without detailed status codes
         return out != null ? "Question added successfully with id: " + out.getId() : "Failed to add question";
    }

    public String updateQuestion(Integer id, Question updatedQuestion) {
        Question existingQuestion = questionRepository.findById(id).orElse(null);
        if (existingQuestion != null) {
            // Updating and saving the existing question
             existingQuestion.setQuestionTitle(updatedQuestion.getQuestionTitle());
             existingQuestion.setOption1(updatedQuestion.getOption1());
             existingQuestion.setOption2(updatedQuestion.getOption2());
             existingQuestion.setOption3(updatedQuestion.getOption3());
             existingQuestion.setOption4(updatedQuestion.getOption4());
             existingQuestion.setCategory(updatedQuestion.getCategory());
             existingQuestion.setRightAnswer(updatedQuestion.getRightAnswer());
             existingQuestion.setDifficultyLevel(updatedQuestion.getDifficultyLevel());
             questionRepository.save(existingQuestion); return "Question updated successfully"; }
        else { return "Question not found"; }
    }

    public String deleteQuestionByName(String name) {
        List<Question> questions = questionRepository.findByQuestionTitle(name);
        if (!questions.isEmpty()) {
            questionRepository.deleteAll(questions);
            return "Questions with name '" + name + "' deleted successfully";
        } else {
            return "Question not found";
        }
    }

    public String deleteQuestionById(Integer id) {
        if (questionRepository.existsById(id)) {
            questionRepository.deleteById(id);
            return "Question deleted successfully"; }
        else { return "Question not found";}
        }


    }
