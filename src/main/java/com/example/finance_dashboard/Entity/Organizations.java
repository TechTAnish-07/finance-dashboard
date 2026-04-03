package com.example.finance_dashboard.Entity;

import com.example.finance_dashboard.DTO.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "organizations")
public class Organizations {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Status status;
    private LocalDateTime createdDate;
    private LocalDateTime statusUpdatedDate;

}
