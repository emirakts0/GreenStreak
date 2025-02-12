package com.emir.gitautocommit.controller;

import com.emir.gitautocommit.dto.AutoCommitRequestDto;
import com.emir.gitautocommit.dto.AutoCommitResponseDto;
import com.emir.gitautocommit.dto.StatusDto;
import com.emir.gitautocommit.record.AutoCommitJobHistoryRecord;
import com.emir.gitautocommit.service.AutoCommitManagementService;
import com.emir.gitautocommit.service.AutoCommitJobHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auto-commit")
public class AutoCommitController {

    private final AutoCommitManagementService autoCommitManagementService;
    private final AutoCommitJobHistoryService jobHistoryService;

    @PostMapping("/schedule")
    public ResponseEntity<AutoCommitResponseDto> scheduleAutoCommit(@Valid @RequestBody AutoCommitRequestDto requestDto) {
        log.debug("received /schedule request.  :  {}", requestDto);
        try {
            requestDto.validateCommitCountRange();
            AutoCommitResponseDto response = autoCommitManagementService.scheduleAutoCommitJob(requestDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/cancel")
    public ResponseEntity<String> cancelAutoCommit() {
        log.debug("received /cancel request.");
        autoCommitManagementService.cancelAutoCommitJob();
        return ResponseEntity.ok("Auto-commit job canceled successfully");
    }

    @GetMapping("/history")
    public ResponseEntity<List<AutoCommitJobHistoryRecord>> getJobHistory() {
        log.debug("received /history request.");
        List<AutoCommitJobHistoryRecord> history = jobHistoryService.getAllJobHistories();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/status")
    public ResponseEntity<StatusDto> getStatus() {
        log.debug("received /status request");
        StatusDto status = autoCommitManagementService.getStatus();
        return ResponseEntity.ok(status);
    }

    @GetMapping("/current-history")
    public ResponseEntity<AutoCommitJobHistoryRecord> getCurrentHistory() {
        log.debug("received /current-history request");
        String activeJobId = autoCommitManagementService.getActiveJobId();
        if (activeJobId != null) {
            AutoCommitJobHistoryRecord jobHistory = jobHistoryService.getJobHistory(activeJobId);
            return ResponseEntity.ok(jobHistory);
        }
        return ResponseEntity.notFound().build();
    }

}
