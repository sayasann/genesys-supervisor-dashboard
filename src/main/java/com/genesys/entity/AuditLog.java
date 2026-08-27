package com.genesys.entity;

import com.genesys.enums.audit.AuditAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@NoArgsConstructor

public class AuditLog {

    public AuditLog(AuditAction action, String actorUsername, String targetUsername) {
        this.action = action;
        this.actorUsername = actorUsername;
        this.targetUsername = targetUsername;
        this.createdAt = Instant.now();
    }

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Column(nullable = false)
    private String actorUsername;

    private String targetUsername;

    @Column(nullable = false)
    private Instant createdAt;
}
