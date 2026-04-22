package com.telusko.quizapp.service;


import com.telusko.quizapp.dao.QuestionRepository;
import com.telusko.quizapp.dao.QuizRepository;
import com.telusko.quizapp.model.Question;
import com.telusko.quizapp.model.QuestionWrapper;
import com.telusko.quizapp.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuestionRepository questionRepository;

     public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
         // Logic to create a quiz based on the category and number of questions
         // You can use the questionRepository to fetch questions based on the category
         // and then save the quiz using quizRepository
         try {
             List<Question> questions = questionRepository.findRandomQuestionsByCategory(category, numQ);

             if (questions.isEmpty()) { return new ResponseEntity<>("Not enough questions available for the given category", HttpStatus.BAD_REQUEST); }

             Quiz quiz = new Quiz();
             quiz.setTitle(title);
             quiz.setQuestions(questions);
             quizRepository.save(quiz);
             return new ResponseEntity<>("Quiz created successfully with title: " + title, HttpStatus.CREATED);
         } catch (Exception e) {
             e.printStackTrace();
             return new ResponseEntity<>("Error occurred while creating the quiz", HttpStatus.INTERNAL_SERVER_ERROR);
         }
     }

    public List<QuestionWrapper> getQuizQuestions(Integer id) {
        Optional<Quiz> quiz = quizRepository.findById(id);
        // Check if the quiz is present
         if (quiz.isPresent()) {
             // Get the questions from the quiz object
              List<Question> questionsFromDB = quiz.get().getQuestions();
              List<QuestionWrapper> questionsForUser = new ArrayList<>();
              // Loop through each question and convert it to QuestionWrapper
              for (Question q : questionsFromDB) {
                   QuestionWrapper qw = new QuestionWrapper (
                            q.getId(),
                            q.getQuestionTitle(),
                            q.getOption1(),
                            q.getOption2(),
                            q.getOption3(),
                            q.getOption4()
                          );
                   questionsForUser.add(qw);
                   // Add each mapped question to the list
                   } return questionsForUser; } else {
             // Handle case when the quiz is not found
              throw new RuntimeException("Quiz not found with id: " + id);
         }
     }

}
