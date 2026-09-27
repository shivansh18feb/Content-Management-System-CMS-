package com.portfolio.cms.about.service;

import com.portfolio.cms.about.dto.AboutDto;

public interface AboutService {
    AboutDto getAbout();
    AboutDto updateAbout(AboutDto dto, Long userId, String userEmail);
}
