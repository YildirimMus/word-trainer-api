package com.wordtrainer.repository;

import com.wordtrainer.model.TrainingSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingSessionRepository extends MongoRepository<TrainingSession, String> {
    List<TrainingSession> findByChildIdOrderByCreatedAtDesc(String childId, Pageable pageable);
    List<TrainingSession> findByChildIdAndListIdOrderByCreatedAtDesc(String childId, String listId, Pageable pageable);
    List<TrainingSession> findByChildId(String childId);
    List<TrainingSession> findByChildIdAndListId(String childId, String listId);
    void deleteByChildId(String childId);
    void deleteByListId(String listId);
}
