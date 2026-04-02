package com.example.finance_dashboard.Entity;

import com.example.finance_dashboard.DTO.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jdk.jshell.Snippet;
import lombok.Data;

import java.util.Date;

@Entity
@lombok.Getter
@lombok.Setter
@Data
@lombok.NoArgsConstructor
@Table(name = "organizations")
public class Organizations {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Status status;
    private Date createdDate;
    private Date statusUpdatedDate;

}
