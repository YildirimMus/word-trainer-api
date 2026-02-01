package com.wordtrainer.service;

import com.wordtrainer.dto.request.SaveTrainingRequest;
import com.wordtrainer.exception.ResourceNotFoundException;
import com.wordtrainer.exception.UnauthorizedException;
import com.wordtrainer.model.Child;
import com.wordtrainer.model.TrainingSession;
import com.wordtrainer.model.WordList;
import com.wordtrainer.repository.ChildRepository;
import com.wordtrainer.repository.TrainingSessionRepository;
import com.wordtrainer.repository.WordListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainingService {

    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);

    private final TrainingSessionRepository sessionRepository;
    private final WordListRepository listRepository;
    private final ChildRepository childRepository;

    public TrainingService(TrainingSessionRepository sessionRepository, WordListRepository listRepository,
                           ChildRepository childRepository) {
        this.sessionRepository = sessionRepository;
        this.listRepository = listRepository;
        this.childRepository = childRepository;
    }

    public List<TrainingSession> getHistory(String childId, String listId, int limit) {
        PageRequest pageable = PageRequest.of(0, limit);
        
        if (listId != null && !listId.isBlank()) {
            return sessionRepository.findByChildIdAndListIdOrderByCreatedAtDesc(childId, listId, pageable);
        }
        return sessionRepository.findByChildIdOrderByCreatedAtDesc(childId, pageable);
    }

    public TrainingSession saveTraining(String childId, String requesterId, String requesterRole, SaveTrainingRequest request) {
        validateAccess(childId, requesterId, requesterRole);

        WordList list = listRepository.findById(request.getListId())
                .orElseThrow(() -> new ResourceNotFoundException("Liste", request.getListId()));

        int correctCount = (int) request.getResults().stream().filter(r -> Boolean.TRUE.equals(r.getCorrect())).count();
        int incorrectCount = request.getResults().size() - correctCount;
        int score = (int) Math.round((double) correctCount / request.getResults().size() * 100);

        List<TrainingSession.Result> results = request.getResults().stream()
                .map(r -> TrainingSession.Result.builder()
                        .word(r.getWord())
                        .userAnswer(r.getUserAnswer())
                        .correct(r.getCorrect())
                        .build())
                .toList();

        TrainingSession session = TrainingSession.builder()
                .childId(childId)
                .listId(list.getId())
                .listName(list.getName())
                .trainingType(request.getTrainingType())
                .totalWords(request.getResults().size())
                .correctCount(correctCount)
                .incorrectCount(incorrectCount)
                .score(score)
                .durationSeconds(request.getDurationSeconds())
                .results(results)
                .build();

        session = sessionRepository.save(session);

        updateChildStats(childId, correctCount, request.getResults().size());

        log.info("Training saved for child: {}, score: {}%", childId, score);
        return session;
    }

    private void updateChildStats(String childId, int correctCount, int totalWords) {
        Child child = childRepository.findById(childId).orElse(null);
        if (child == null) return;

        Child.Stats stats = child.getStats();
        stats.setTotalTrainings(stats.getTotalTrainings() + 1);
        stats.setTotalWords(stats.getTotalWords() + totalWords);
        stats.setCorrectWords(stats.getCorrectWords() + correctCount);

        Instant lastTraining = stats.getLastTrainingDate();
        Instant now = Instant.now();
        
        if (lastTraining == null) {
            stats.setStreak(1);
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastTraining, now);
            if (daysBetween <= 1) {
                stats.setStreak(stats.getStreak() + 1);
            } else {
                stats.setStreak(1);
            }
        }
        
        stats.setLastTrainingDate(now);
        child.setStats(stats);
        childRepository.save(child);
    }

    public Map<String, Object> getChildStats(String childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", childId));

        Child.Stats stats = child.getStats();
        int successRate = stats.getTotalWords() > 0 
                ? (int) Math.round((double) stats.getCorrectWords() / stats.getTotalWords() * 100)
                : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("totalTrainings", stats.getTotalTrainings());
        result.put("totalWords", stats.getTotalWords());
        result.put("correctWords", stats.getCorrectWords());
        result.put("successRate", successRate);
        result.put("streak", stats.getStreak());
        result.put("lastTrainingDate", stats.getLastTrainingDate());

        return result;
    }

    public List<Map<String, Object>> getFailedWords(String childId, String listId, int limit) {
        List<TrainingSession> sessions;
        
        if (listId != null && !listId.isBlank()) {
            sessions = sessionRepository.findByChildIdAndListId(childId, listId);
        } else {
            sessions = sessionRepository.findByChildId(childId);
        }

        Map<String, Integer> errorCounts = new HashMap<>();
        
        for (TrainingSession session : sessions) {
            for (TrainingSession.Result result : session.getResults()) {
                if (!Boolean.TRUE.equals(result.getCorrect())) {
                    errorCounts.merge(result.getWord(), 1, Integer::sum);
                }
            }
        }

        return errorCounts.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("word", e.getKey());
                    item.put("count", e.getValue());
                    return item;
                })
                .toList();
    }

    private void validateAccess(String childId, String requesterId, String requesterRole) {
        if ("child".equals(requesterRole)) {
            if (!childId.equals(requesterId)) {
                throw new UnauthorizedException("Accès non autorisé");
            }
        } else if ("parent".equals(requesterRole)) {
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new ResourceNotFoundException("Enfant", childId));
            if (!child.getParentId().equals(requesterId)) {
                throw new UnauthorizedException("Accès non autorisé");
            }
        }
    }
}
