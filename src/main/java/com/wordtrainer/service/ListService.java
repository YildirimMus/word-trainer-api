package com.wordtrainer.service;

import com.wordtrainer.dto.request.CreateListRequest;
import com.wordtrainer.exception.ResourceNotFoundException;
import com.wordtrainer.exception.UnauthorizedException;
import com.wordtrainer.model.Child;
import com.wordtrainer.model.WordList;
import com.wordtrainer.repository.ChildRepository;
import com.wordtrainer.repository.TrainingSessionRepository;
import com.wordtrainer.repository.WordListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListService {

    private static final Logger log = LoggerFactory.getLogger(ListService.class);

    private final WordListRepository listRepository;
    private final ChildRepository childRepository;
    private final TrainingSessionRepository sessionRepository;

    public ListService(WordListRepository listRepository, ChildRepository childRepository,
                       TrainingSessionRepository sessionRepository) {
        this.listRepository = listRepository;
        this.childRepository = childRepository;
        this.sessionRepository = sessionRepository;
    }

    public List<WordList> getListsForChild(String childId) {
        return listRepository.findByChildId(childId);
    }

    public WordList getList(String listId) {
        return listRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Liste", listId));
    }

    public WordList createList(String childId, String requesterId, String requesterRole, CreateListRequest request) {
        validateAccess(childId, requesterId, requesterRole);

        List<String> cleanedWords = request.getWords().stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(w -> !w.isBlank())
                .distinct()
                .toList();

        WordList list = WordList.builder()
                .childId(childId)
                .name(request.getName().trim())
                .words(cleanedWords)
                .build();

        list = listRepository.save(list);
        log.info("New list created: {} for child: {}", list.getName(), childId);

        return list;
    }

    public WordList updateList(String listId, String requesterId, String requesterRole, CreateListRequest request) {
        WordList list = listRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Liste", listId));

        validateAccess(list.getChildId(), requesterId, requesterRole);

        List<String> cleanedWords = request.getWords().stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(w -> !w.isBlank())
                .distinct()
                .toList();

        list.setName(request.getName().trim());
        list.setWords(cleanedWords);

        list = listRepository.save(list);
        log.info("List updated: {}", listId);

        return list;
    }

    @Transactional
    public void deleteList(String listId, String requesterId, String requesterRole) {
        WordList list = listRepository.findById(listId)
                .orElseThrow(() -> new ResourceNotFoundException("Liste", listId));

        validateAccess(list.getChildId(), requesterId, requesterRole);

        sessionRepository.deleteByListId(listId);
        listRepository.delete(list);
        log.info("List deleted: {}", listId);
    }

    private void validateAccess(String childId, String requesterId, String requesterRole) {
        if ("child".equals(requesterRole)) {
            if (!childId.equals(requesterId)) {
                throw new UnauthorizedException("Accès non autorisé à cette liste");
            }
        } else if ("parent".equals(requesterRole)) {
            Child child = childRepository.findById(childId)
                    .orElseThrow(() -> new ResourceNotFoundException("Enfant", childId));
            if (!child.getParentId().equals(requesterId)) {
                throw new UnauthorizedException("Accès non autorisé à cette liste");
            }
        }
    }
}
