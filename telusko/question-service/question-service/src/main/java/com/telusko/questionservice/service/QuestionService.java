package com.telusko.questionservice.service;


import com.telusko.questionservice.dao.QuestionRepository;
import com.telusko.questionservice.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;


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
