package com.lionelrivas.repository;

import com.lionelrivas.entity.WorkInProgress;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

public interface RepoWithCode {
    Optional<WorkInProgress> findById(Integer id);

    @Transactional
    default void updateStatus(Integer id, String status) {
        WorkInProgress workInProgress = findById(id).orElseThrow(() -> new EntityNotFoundException(""));
        workInProgress.setStatus(status);
    }

    default WorkInProgress findWorkInProgress(Integer id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException(""));
    }
}
