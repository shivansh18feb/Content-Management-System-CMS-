package com.portfolio.cms.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.contact.dto.ContactMessageDto;
import com.portfolio.cms.contact.dto.ContactRequest;
import com.portfolio.cms.contact.entity.ContactMessage;
import com.portfolio.cms.contact.entity.MessageStatus;
import com.portfolio.cms.contact.repository.ContactMessageRepository;
import com.portfolio.cms.contact.service.ContactServiceImpl;
import com.portfolio.cms.contact.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactMessageRepository repository;

    @Mock
    private EmailService emailService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ContactServiceImpl contactService;

    private ContactRequest validRequest;
    private ContactMessage sampleMessage;

    @BeforeEach
    void setUp() {
        validRequest = ContactRequest.builder()
                .name("Alice Walker")
                .email("alice@techcorp.com")
                .subject("Senior Backend Opportunity")
                .message("We would love to discuss a Staff Engineer position.")
                .build();

        sampleMessage = ContactMessage.builder()
                .name("Alice Walker")
                .email("alice@techcorp.com")
                .subject("Senior Backend Opportunity")
                .message("We would love to discuss a Staff Engineer position.")
                .status(MessageStatus.UNREAD)
                .ipAddress("127.0.0.1")
                .build();
        sampleMessage.setId(10L);
        sampleMessage.setCreatedAt(Instant.now());
    }

    @Test
    @DisplayName("submitMessage should save entity, trigger email notification, and return DTO")
    void submitMessage_Success() {
        when(repository.save(any(ContactMessage.class))).thenReturn(sampleMessage);

        ContactMessageDto result = contactService.submitMessage(validRequest, "127.0.0.1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("Alice Walker");
        assertThat(result.getStatus()).isEqualTo(MessageStatus.UNREAD);

        verify(repository, times(1)).save(any(ContactMessage.class));
        verify(emailService, times(1)).sendContactNotification(
                eq("Alice Walker"),
                eq("alice@techcorp.com"),
                eq("Senior Backend Opportunity"),
                anyString()
        );
    }

    @Test
    @DisplayName("getMessageById should return message when found")
    void getMessageById_Found() {
        when(repository.findById(10L)).thenReturn(Optional.of(sampleMessage));

        ContactMessageDto result = contactService.getMessageById(10L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getEmail()).isEqualTo("alice@techcorp.com");
    }

    @Test
    @DisplayName("getMessageById should throw ResourceNotFoundException when message does not exist")
    void getMessageById_NotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.getMessageById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Message not found with id: 999");
    }

    @Test
    @DisplayName("deleteMessage should remove entity and record audit log")
    void deleteMessage_Success() {
        when(repository.findById(10L)).thenReturn(Optional.of(sampleMessage));

        contactService.deleteMessage(10L, 1L, "admin@portfolio.com");

        verify(repository, times(1)).delete(sampleMessage);
        verify(auditLogService, times(1)).log(
                eq(1L),
                eq("admin@portfolio.com"),
                eq("DELETE"),
                eq("ContactMessage"),
                eq("10"),
                any(),
                anyString()
        );
    }
}
