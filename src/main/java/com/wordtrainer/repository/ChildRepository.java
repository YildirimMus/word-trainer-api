package com.wordtrainer.repository;

import com.wordtrainer.model.Child;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChildRepository extends MongoRepository<Child, String> {
    List<Child> findByParentId(String parentId);
    Optional<Child> findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByParentId(String parentId);
}
