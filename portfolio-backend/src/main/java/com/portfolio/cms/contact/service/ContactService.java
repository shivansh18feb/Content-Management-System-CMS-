package com.portfolio.cms.contact.service;

import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.contact.dto.ContactMessageDto;
import com.portfolio.cms.contact.dto.ContactRequest;
import com.portfolio.cms.contact.entity.MessageStatus;
import org.springframework.data.domain.Pageable;

public interface ContactService {
    ContactMessageDto submitMessage(ContactRequest request, String clientIp);
    PagedResponse<ContactMessageDto> getMessagesAdmin(String search, MessageStatus status, Pageable pageable);
    ContactMessageDto getMessageById(Long id);
    ContactMessageDto markAsRead(Long id, Long userId, String userEmail);
    ContactMessageDto markAsArchived(Long id, Long userId, String userEmail);
    void deleteMessage(Long id, Long userId, String userEmail);
    long getUnreadCount();
}
