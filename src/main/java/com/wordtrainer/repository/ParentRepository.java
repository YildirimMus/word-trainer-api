package com.wordtrainer.repository;

import com.wordtrainer.model.Parent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParentRepository extends MongoRepository<Parent, String> {
    Optional<Parent> findByEmail(String email);
    boolean existsByEmail(String email);
}
