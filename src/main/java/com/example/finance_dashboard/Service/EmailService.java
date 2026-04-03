package com.example.finance_dashboard.Service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface EmailService {


    @Async
    @Transactional
    void sendLoginLink(Long userId);
}
