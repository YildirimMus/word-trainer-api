package com.wordtrainer.repository;

import com.wordtrainer.model.WordList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordListRepository extends MongoRepository<WordList, String> {
    List<WordList> findByChildId(String childId);
    void deleteByChildId(String childId);
    long countByChildId(String childId);
}
