package com.genesys.service;

import com.genesys.entity.AuditLog;
import com.genesys.enums.audit.AuditAction;
import com.genesys.repo.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void record(AuditAction action, String actor, String target){
        auditLogRepository.save(new AuditLog(action,actor,target));
    }

    public Page<AuditLog> fetchLogs(int page,int size){
        Pageable pageable = PageRequest.of(page,size);
        return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
    }
}
