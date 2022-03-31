package com.lionelrivas.service;

import com.lionelrivas.entity.WorkInProgress;
import com.lionelrivas.repository.WorkInProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
public class WorkInProgressService {

    private final WorkInProgressRepository workInProgressRepository;

    @Autowired
    public WorkInProgressService(WorkInProgressRepository workInProgressRepository) {
        this.workInProgressRepository = workInProgressRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateStatus(Integer id, String status) {
        WorkInProgress workInProgress = workInProgressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unable to find record with id " + id));
        workInProgress.setStatus(status);
        workInProgressRepository.save(workInProgress);

    }

    @Transactional(readOnly = true)
    public WorkInProgress findById(Integer id) {
        return workInProgressRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Unable to find record with id " + id));
    }
}
