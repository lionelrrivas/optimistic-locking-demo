package com.lionelrivas.service;

import com.lionelrivas.entity.WorkInProgress;
import com.lionelrivas.repository.WorkInProgressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class WorkInProgressServiceTest {

    @Autowired
    WorkInProgressRepository workInProgressRepository;

    @SpyBean
    WorkInProgressService workInProgressService;

    private static final List<String> statuses = Arrays.asList("EXCEPTION", "RELEASED");

    @BeforeEach
    void tearDown() {
        workInProgressRepository.deleteAll();
    }

    @Test
    void updateStatus_consecutive_updates_in_same_thread_no_exception() {
        WorkInProgress workInProgress = new WorkInProgress();
        workInProgress.setStatus("QUEUED");
        final WorkInProgress beginningWorkInProgress = workInProgressRepository.save(workInProgress);
//        assertEquals(0, beginningWorkInProgress.getVersion());

        statuses.forEach(status -> workInProgressService.updateStatus(beginningWorkInProgress.getId(), status));

        WorkInProgress endingWorkInProgress = workInProgressService.findById(beginningWorkInProgress.getId());
        assertAll(
//                () ->assertEquals(2, endingWorkInProgress.getVersion()),
                () -> assertEquals("RELEASED", endingWorkInProgress.getStatus()),
                () -> verify(workInProgressService, times(2)).updateStatus(anyInt(), anyString())
        );
    }

    @Test
    void updateStatus_concurrent_updates_in_different_threads_throws_exception() throws Exception {
        WorkInProgress saved = new WorkInProgress();
        saved.setStatus("QUEUED");
        final WorkInProgress workInProgress = workInProgressRepository.save(saved);
        assertEquals(0, workInProgress.getVersion());

        final ExecutorService executor = Executors.newFixedThreadPool(statuses.size());
        statuses.forEach(status -> executor.execute(() -> workInProgressService.updateStatus(workInProgress.getId(), status)));

//        executor.execute(() -> workInProgressService.updateStatus(workInProgress.getId(), "EXCEPTION"));
        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES));

        assertThrows(ObjectOptimisticLockingFailureException.class, () -> workInProgressRepository.save(workInProgress));

        final WorkInProgress endingWorkInProgress = workInProgressService.findById(workInProgress.getId());
        assertAll(
                () -> assertEquals(1, endingWorkInProgress.getVersion()),
                () -> assertTrue(statuses.contains(endingWorkInProgress.getStatus()))
        );
    }
}