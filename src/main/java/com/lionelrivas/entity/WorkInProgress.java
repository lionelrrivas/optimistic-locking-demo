package com.lionelrivas.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "work_in_progress", schema = "isdc_schema")
public class WorkInProgress {

    @Version
    private Long version;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "work_in_progress_seq")
    @SequenceGenerator(name = "work_in_progress_seq", sequenceName = "work_in_progress_seq")
    private Integer id;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "status")
    private String status;

    @Column(name = "employee_id")
    private int employeeId;

}
