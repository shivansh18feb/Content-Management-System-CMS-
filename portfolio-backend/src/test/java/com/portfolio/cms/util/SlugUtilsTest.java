package com.portfolio.cms.util;

import com.portfolio.cms.common.util.SlugUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlugUtilsTest {

    @Test
    @DisplayName("Should convert plain title into lowercase hyphenated slug")
    void shouldConvertBasicTitleToSlug() {
        String result = SlugUtils.toSlug("Cloud-Native Distributed Storage Platform");
        assertThat(result).isEqualTo("cloud-native-distributed-storage-platform");
    }

    @Test
    @DisplayName("Should strip special characters and punctuation")
    void shouldStripSpecialCharacters() {
        String result = SlugUtils.toSlug("Hello World! 2026: The Ultimate Guide #1?");
        assertThat(result).isEqualTo("hello-world-2026-the-ultimate-guide-1");
    }

    @Test
    @DisplayName("Should normalize accented characters")
    void shouldNormalizeAccents() {
        String result = SlugUtils.toSlug("Café & Résumé Performance");
        assertThat(result).isEqualTo("cafe-resume-performance");
    }

    @Test
    @DisplayName("Should collapse consecutive whitespace and hyphens")
    void shouldCollapseConsecutiveHyphens() {
        String result = SlugUtils.toSlug("  Spring   Boot --- Microservices   ");
        assertThat(result).isEqualTo("spring-boot-microservices");
    }

    @Test
    @DisplayName("Should return empty string for null or empty input")
    void shouldHandleNullOrBlank() {
        assertThat(SlugUtils.toSlug(null)).isEmpty();
        assertThat(SlugUtils.toSlug("   ")).isEmpty();
    }
}
