package com.portfolio.cms.contact.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.contact.dto.ContactMessageDto;
import com.portfolio.cms.contact.dto.ContactRequest;
import com.portfolio.cms.contact.service.ContactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/contact")
@RequiredArgsConstructor
@Tag(name = "Public Contact", description = "Public endpoint for submitting inquiries from the portfolio contact form")
public class ContactPublicController {

    private final ContactService contactService;

    @PostMapping
    @Operation(summary = "Submit Contact Inquiry", description = "Validates inquiry, applies spam protection, persists message, and dispatches email alert")
    public ResponseEntity<ApiResponse<ContactMessageDto>> submitContact(
            @Valid @RequestBody ContactRequest request,
            HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        ContactMessageDto message = contactService.submitMessage(request, clientIp);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Thank you for reaching out! Your message has been sent successfully.", message));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
