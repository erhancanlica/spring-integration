package com.aaroen.spring.integration.persistence.entity;

import com.aaroen.spring.integration.model.enums.SendStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "EMAILS", schema = "SPRING_INTEGRATION_API_APP_ADM")
@SequenceGenerator(name = "sequenceGenerator", sequenceName = "SPRING_INTEGRATION_API_APP_ADM.EMAILS_SEQ", allocationSize = 1)
public class EmailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    private Long id;
    private Long senderId;
    private String senderName;
    private String subject;
    private String message;
    private String attachment;
    private String recipientEmail;
    private Integer retryCount;
    private LocalDateTime sendDate;
    @Enumerated(EnumType.STRING)
    private SendStatusEnum sendStatus;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}