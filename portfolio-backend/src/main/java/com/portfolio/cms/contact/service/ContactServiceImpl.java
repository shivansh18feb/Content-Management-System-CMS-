package com.portfolio.cms.contact.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.contact.dto.ContactMessageDto;
import com.portfolio.cms.contact.dto.ContactRequest;
import com.portfolio.cms.contact.entity.ContactMessage;
import com.portfolio.cms.contact.entity.MessageStatus;
import com.portfolio.cms.contact.repository.ContactMessageRepository;
import com.portfolio.cms.common.util.PageableUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

    private final ContactMessageRepository repository;
    private final EmailService emailService;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public ContactMessageDto submitMessage(ContactRequest request, String clientIp) {
        ContactMessage message = ContactMessage.builder()
                .name(request.getName().trim())
                .email(request.getEmail().toLowerCase().trim())
                .subject(request.getSubject().trim())
                .message(request.getMessage().trim())
                .status(MessageStatus.UNREAD)
                .ipAddress(clientIp)
                .build();

        ContactMessage saved = repository.save(message);

        // Dispatch email notification asynchronously/safely
        emailService.sendContactNotification(saved.getName(), saved.getEmail(), saved.getSubject(), saved.getMessage());

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ContactMessageDto> getMessagesAdmin(String search, MessageStatus status, Pageable pageable) {
        String statusStr = status != null ? status.name() : null;
        Pageable snakePageable = PageableUtils.toSnakeCase(pageable, "created_at", Sort.Direction.DESC);
        Page<ContactMessage> page = repository.findWithFilters(search, statusStr, snakePageable);
        return PagedResponse.of(page.map(this::mapToDto));
    }

    @Override
    @Transactional(readOnly = true)
    public ContactMessageDto getMessageById(Long id) {
        ContactMessage message = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        return mapToDto(message);
    }

    @Override
    @Transactional
    public ContactMessageDto markAsRead(Long id, Long userId, String userEmail) {
        ContactMessage message = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        message.setStatus(MessageStatus.READ);
        ContactMessage saved = repository.save(message);
        auditLogService.log(userId, userEmail, "READ_MESSAGE", "ContactMessage", String.valueOf(saved.getId()), null, "Marked message as READ: " + saved.getSubject());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ContactMessageDto markAsArchived(Long id, Long userId, String userEmail) {
        ContactMessage message = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        message.setStatus(MessageStatus.ARCHIVED);
        ContactMessage saved = repository.save(message);
        auditLogService.log(userId, userEmail, "ARCHIVE_MESSAGE", "ContactMessage", String.valueOf(saved.getId()), null, "Archived message: " + saved.getSubject());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id, Long userId, String userEmail) {
        ContactMessage message = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
        String subject = message.getSubject();
        repository.delete(message);
        auditLogService.log(userId, userEmail, "DELETE", "ContactMessage", String.valueOf(id), null, "Deleted message: " + subject);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return repository.countByStatus(MessageStatus.UNREAD);
    }

    private ContactMessageDto mapToDto(ContactMessage m) {
        return ContactMessageDto.builder()
                .id(m.getId())
                .name(m.getName())
                .email(m.getEmail())
                .subject(m.getSubject())
                .message(m.getMessage())
                .status(m.getStatus())
                .ipAddress(m.getIpAddress())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
