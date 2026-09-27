package com.portfolio.cms.contact.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.contact.dto.ContactMessageDto;
import com.portfolio.cms.contact.entity.MessageStatus;
import com.portfolio.cms.contact.service.ContactService;
import com.portfolio.cms.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Messages CMS", description = "CMS endpoints for moderating and reviewing visitor inquiries")
public class MessageAdminController {

    private final ContactService contactService;

    @GetMapping
    @Operation(summary = "List Messages with Filter and Search")
    public ResponseEntity<ApiResponse<PagedResponse<ContactMessageDto>>> getMessages(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) MessageStatus status,
            @PageableDefault(size = 15) Pageable pageable) {
        PagedResponse<ContactMessageDto> response = contactService.getMessagesAdmin(search, status, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Message by ID")
    public ResponseEntity<ApiResponse<ContactMessageDto>> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(contactService.getMessageById(id)));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark Message as Read")
    public ResponseEntity<ApiResponse<ContactMessageDto>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ContactMessageDto message = contactService.markAsRead(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Message marked as read", message));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive Message")
    public ResponseEntity<ApiResponse<ContactMessageDto>> markAsArchived(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ContactMessageDto message = contactService.markAsArchived(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Message archived", message));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Message")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        contactService.deleteMessage(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Message deleted successfully"));
    }
}
