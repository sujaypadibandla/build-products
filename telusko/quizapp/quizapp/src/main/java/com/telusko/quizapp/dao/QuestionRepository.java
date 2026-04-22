package com.telusko.quizapp.dao;

import com.telusko.quizapp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    List<Question> findByCategory(String category);

    public List<Question> findByQuestionTitle(String questionTitle);

    @Query(
            value = "SELECT * FROM question WHERE category = :category " +
                    "ORDER BY DBMS_RANDOM.VALUE " +
                    "FETCH FIRST :numQ ROWS ONLY",
            nativeQuery = true
    )
    List<Question> findRandomQuestionsByCategory(@Param("category") String category,
                                                 @Param("numQ") int numQ);


}
