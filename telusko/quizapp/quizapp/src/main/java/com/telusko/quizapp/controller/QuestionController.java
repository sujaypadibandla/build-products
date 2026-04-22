package com.telusko.quizapp.controller;


import com.telusko.quizapp.model.Question;
import com.telusko.quizapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("question")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @GetMapping("allQuestions")
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("category/{category}")
    public List<Question> getQuestionsByCategory(@PathVariable String category) {
        return questionService.getQuestionsByCategory(category);
    }

    @PostMapping("add")
    public String addQuestion(@RequestBody Question question) {
        return questionService.addQuestion(question);
    }

    // Update a question by ID
     @PutMapping("update/{id}")
     public String updateQuestion(@PathVariable Integer id, @RequestBody Question question) {
//         Directly returning the update message (success or failure)
       return questionService.updateQuestion(id, question);
    }

    // Delete a question by name
     @DeleteMapping("delete/name/{name}")
     public String deleteQuestionByName(@PathVariable String name) {
    // Returning the deletion result (success or failure)
     return questionService.deleteQuestionByName(name);
     }

         // Delete a question by ID
          @DeleteMapping("delete/{id}")
         public String deleteQuestionById(@PathVariable Integer id) {
              // Returning the deletion result (success or failure)
              return questionService.deleteQuestionById(id);
     }

}
