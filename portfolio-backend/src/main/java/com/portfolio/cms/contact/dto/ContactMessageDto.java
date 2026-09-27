package com.portfolio.cms.contact.dto;

import com.portfolio.cms.contact.entity.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessageDto {
    private Long id;
    private String name;
    private String email;
    private String subject;
    private String message;
    private MessageStatus status;
    private String ipAddress;
    private Instant createdAt;
}
