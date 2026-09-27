-- ==============================================================================
-- V2__seed_initial_data.sql: Seed initial administrator and portfolio content
-- ==============================================================================

-- 1. Initial Admin User (Default password: Admin@123456, updated by PasswordEncoder if needed)
-- BCrypt 10 rounds for 'Admin@123456': $2a$10$7Z8x3O/qg/b2r9vj0C8Lg.dG6Z8zC1K0p5qL5d3fR.4hW2Q8b2mJy
INSERT INTO users (email, password_hash, full_name, role, enabled, account_locked)
VALUES (
    'admin@portfolio.com',
    '$2a$10$7Z8x3O/qg/b2r9vj0C8Lg.dG6Z8zC1K0p5qL5d3fR.4hW2Q8b2mJy',
    'System Administrator',
    'ADMIN',
    TRUE,
    FALSE
) ON CONFLICT (email) DO NOTHING;

-- 2. About Me Profile
INSERT INTO about (headline, short_bio, long_bio, profile_image_url, location, email, phone, resume_url, availability, years_of_experience, github_url, linkedin_url, twitter_url)
VALUES (
    'Senior Full-Stack Engineer & Distributed Systems Architect',
    'Crafting high-throughput, resilient cloud architectures and elegant user interfaces.',
    'I am a senior full-stack software engineer with deep expertise in Java, Spring Boot, distributed microservices, PostgreSQL, and modern frontend ecosystems like Next.js, React, and TypeScript. Over the past 7+ years, I have architected and built mission-critical web applications, high-performance financial systems, and cloud-native SaaS platforms that scale gracefully under demanding workloads.',
    'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=600&q=80',
    'San Francisco, CA (Open to Remote Worldwide)',
    'shivam@developer.io',
    '+1 (555) 234-5678',
    'https://portfolio-assets.com/docs/resume.pdf',
    'AVAILABLE',
    7,
    'https://github.com',
    'https://linkedin.com',
    'https://twitter.com'
) ON CONFLICT DO NOTHING;

-- 3. Skill Categories
INSERT INTO skill_categories (id, name, display_order) VALUES
(1, 'Backend & Distributed Systems', 1),
(2, 'Frontend & Mobile', 2),
(3, 'Databases & Storage', 3),
(4, 'DevOps & Cloud Infrastructure', 4),
(5, 'Architecture & Best Practices', 5)
ON CONFLICT (id) DO NOTHING;

-- Reset sequence for skill_categories
SELECT setval('skill_categories_id_seq', (SELECT MAX(id) FROM skill_categories));

-- 4. Skills
INSERT INTO skills (category_id, name, icon, proficiency, years_of_experience, display_order, featured, active) VALUES
(1, 'Java 21 / Spring Boot 3', 'java', 95, 7.0, 1, TRUE, TRUE),
(1, 'Spring Security & OAuth2 / JWT', 'shield', 92, 6.0, 2, TRUE, TRUE),
(1, 'Microservices & REST APIs', 'server', 95, 7.0, 3, TRUE, TRUE),
(1, 'Kafka & Event-Driven Architecture', 'activity', 85, 4.0, 4, FALSE, TRUE),
(2, 'React & Next.js 14/15', 'layers', 90, 5.0, 1, TRUE, TRUE),
(2, 'TypeScript', 'code', 92, 5.0, 2, TRUE, TRUE),
(2, 'Tailwind CSS & Modern UI/UX', 'palette', 90, 4.0, 3, FALSE, TRUE),
(3, 'PostgreSQL & Database Normalization', 'database', 92, 6.0, 1, TRUE, TRUE),
(3, 'Redis & Distributed Caching', 'cpu', 88, 5.0, 2, FALSE, TRUE),
(3, 'Hibernate / JPA Query Optimization', 'zap', 90, 6.0, 3, FALSE, TRUE),
(4, 'Docker & Container Orchestration', 'box', 90, 5.0, 1, TRUE, TRUE),
(4, 'Kubernetes & Helm', 'cloud', 82, 3.5, 2, FALSE, TRUE),
(4, 'CI/CD Pipelines (GitHub Actions)', 'git-merge', 88, 4.5, 3, FALSE, TRUE),
(5, 'Clean Architecture & SOLID', 'compass', 95, 7.0, 1, TRUE, TRUE),
(5, 'System Design & Scalability', 'layout', 92, 6.0, 2, TRUE, TRUE);

-- 5. Featured Projects
INSERT INTO projects (id, title, slug, short_description, description, thumbnail_url, github_url, live_url, documentation_url, category, featured, status, display_order, start_date, end_date, seo_title, seo_description)
VALUES
(
    1,
    'Cloud-Native Distributed Meta-File Storage Platform',
    'cloud-native-meta-file-storage',
    'Enterprise-grade multi-tenant file orchestration engine supporting chunked streaming, deduplication, and S3-compatible cold storage.',
    'A high-concurrency cloud metadata storage and replication platform built using Java 21, Spring Boot, PostgreSQL, and S3. Features zero-downtime file encryption at rest, multipart parallel streaming, granular permission policies, and real-time chunk verification.',
    'https://images.unsplash.com/photo-1558494949-ef010cbdcc31?auto=format&fit=crop&w=1200&q=80',
    'https://github.com/example/cloud-storage',
    'https://cloud-storage.demo.io',
    'https://docs.cloud-storage.demo.io',
    'Cloud & Infrastructure',
    TRUE,
    'PUBLISHED',
    1,
    '2024-01-15',
    '2024-06-30',
    'Cloud-Native Meta-File Storage Engine | Case Study',
    'In-depth architectural breakdown of an enterprise cloud file storage and metadata engine.'
),
(
    2,
    'AI Code Review & Security Analysis Engine',
    'ai-code-review-assistant',
    'Automated AST-driven static analysis pipeline and LLM reviewer for detecting security flaws, anti-patterns, and memory leaks in pull requests.',
    'An intelligent continuous inspection engine that integrates with GitHub webhooks to analyze PR diffs in real time. Combines AST parsing with local LLM models to provide high-precision code recommendations, security vulnerability scanning, and automated fix generation.',
    'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=1200&q=80',
    'https://github.com/example/ai-code-review',
    'https://code-review.demo.io',
    'https://docs.code-review.demo.io',
    'Artificial Intelligence & DevOps',
    TRUE,
    'PUBLISHED',
    2,
    '2024-07-01',
    '2024-11-20',
    'AI-Powered Code Review Engine | Architecture',
    'Building an automated AST and LLM powered static code analysis assistant.'
),
(
    3,
    'Core Insurance Underwriting & Policy Lifecycle Engine',
    'insurance-underwriting-platform',
    'Event-driven insurance core handling underwriting rule engines, real-time risk assessment, and claims settlement workflows.',
    'Comprehensive insurance lifecycle platform engineered for high-availability transaction processing. Implements event-sourcing with Kafka, PostgreSQL database partitioning for millions of historical policy records, and role-based adjudicator portals.',
    'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40?auto=format&fit=crop&w=1200&q=80',
    'https://github.com/example/insurance-platform',
    'https://insurance.demo.io',
    'https://docs.insurance.demo.io',
    'FinTech & Enterprise',
    TRUE,
    'PUBLISHED',
    3,
    '2023-03-01',
    '2023-12-15',
    'Insurance Policy Lifecycle Core Platform',
    'Architecting an event-driven insurance underwriting and policy claims system.'
)
ON CONFLICT (id) DO NOTHING;

SELECT setval('projects_id_seq', (SELECT MAX(id) FROM projects));

-- Project Technologies
INSERT INTO project_technologies (project_id, technology) VALUES
(1, 'Java 21'), (1, 'Spring Boot 3'), (1, 'PostgreSQL'), (1, 'AWS S3'), (1, 'Docker'),
(2, 'Java 21'), (2, 'Spring WebFlux'), (2, 'Python / Ollama'), (2, 'React'), (2, 'PostgreSQL'),
(3, 'Java'), (3, 'Spring Boot'), (3, 'Kafka'), (3, 'PostgreSQL'), (3, 'Redis')
ON CONFLICT DO NOTHING;

-- 6. Experience
INSERT INTO experiences (company, position, description, location, employment_type, start_date, end_date, is_current, technologies, display_order)
VALUES
(
    'Stratos Cloud Technologies',
    'Staff Software Architect',
    'Leading backend architecture for multi-region cloud services. Mentoring 15+ engineers, driving zero-downtime migration of legacy monolithic services to reactive microservices, and reducing API p99 latency by 45%.',
    'San Francisco, CA',
    'FULL_TIME',
    '2022-04-01',
    NULL,
    TRUE,
    'Java 21, Spring Boot 3, Kubernetes, PostgreSQL, Kafka, Redis',
    1
),
(
    'Apex Financial Systems',
    'Senior Backend Engineer',
    'Designed and implemented real-time fraud detection and high-volume ledger accounting pipelines processing $50M+ daily transactions with strict ACID guarantees.',
    'New York, NY',
    'FULL_TIME',
    '2019-06-01',
    '2022-03-31',
    FALSE,
    'Java, Spring Data JPA, PostgreSQL, Docker, AWS, JUnit 5',
    2
),
(
    'Nexus Digital Lab',
    'Full-Stack Developer',
    'Built full-stack web applications and internal tools using React, TypeScript, and Spring Boot REST APIs for Fortune 500 clients.',
    'Boston, MA',
    'FULL_TIME',
    '2017-08-01',
    '2019-05-31',
    FALSE,
    'React, TypeScript, Java, Spring Boot, MySQL, Docker',
    3
);

-- 7. Education
INSERT INTO education (institution, degree, field_of_study, description, start_date, end_date, grade, location, display_order)
VALUES
(
    'Massachusetts Institute of Technology (MIT)',
    'Bachelor of Science',
    'Computer Science & Engineering',
    'Specialized in Distributed Systems, Algorithms, and Software Security. Graduated Magna Cum Laude.',
    '2013-09-01',
    '2017-05-31',
    '3.92 / 4.0 GPA',
    'Cambridge, MA',
    1
);

-- 8. Services
INSERT INTO services (title, description, icon, features, display_order, active)
VALUES
(
    'Enterprise Full-Stack Web Development',
    'End-to-end development of scalable web platforms with bulletproof Spring Boot backends and dynamic, responsive Next.js/React frontends.',
    'code',
    'Clean Architecture & SOLID principles,Type-safe TypeScript & Java 21,Responsive mobile-first UI with Tailwind CSS,Comprehensive automated test suites',
    1,
    TRUE
),
(
    'Distributed Systems & Cloud Architecture',
    'Architecting high-throughput, fault-tolerant cloud infrastructures, database scaling, microservices decomposition, and event-driven backbones.',
    'cloud',
    'Containerization with Docker & Kubernetes,Event streaming with Apache Kafka,Database partitioning & index optimization,Resilience patterns (Circuit Breakers, Rate Limiting)',
    2,
    TRUE
),
(
    'Security Auditing & Performance Optimization',
    'Identifying bottlenecks, optimizing SQL queries, hardening API authentication, and conducting thorough security audits.',
    'shield',
    'Zero-trust JWT & OAuth2 RBAC implementations,SQL profiling & N+1 query elimination,Memory leak detection & JVM tuning,OWASP Top 10 remediation',
    3,
    TRUE
);

-- 9. Testimonials
INSERT INTO testimonials (name, role, company, avatar_url, content, rating, featured, published, display_order)
VALUES
(
    'Elena Rostova',
    'VP of Engineering',
    'CloudScale Systems',
    'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?auto=format&fit=crop&w=400&q=80',
    'An exceptional software architect. He stepped in during our critical microservice migration and delivered an architecture that was remarkably clean, robust, and fast. His deep grasp of Spring Boot and database internals is second to none.',
    5,
    TRUE,
    TRUE,
    1
),
(
    'Marcus Vance',
    'Chief Technology Officer',
    'Vanguard Payments',
    'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80',
    'Working together was a breath of fresh air. From initial API design to high-throughput performance tuning, everything was executed with meticulous attention to detail and rigorous automated testing.',
    5,
    TRUE,
    TRUE,
    2
);

-- 10. Blog Categories & Tags
INSERT INTO blog_categories (id, name, slug, description)
VALUES
(1, 'Software Architecture', 'software-architecture', 'Architectural patterns, domain modeling, and distributed systems.'),
(2, 'Backend Engineering', 'backend-engineering', 'Deep dives into Java, Spring Boot, concurrency, and databases.')
ON CONFLICT (id) DO NOTHING;

SELECT setval('blog_categories_id_seq', (SELECT MAX(id) FROM blog_categories));

INSERT INTO blog_tags (id, name, slug)
VALUES
(1, 'Java 21', 'java-21'),
(2, 'Spring Boot', 'spring-boot'),
(3, 'Architecture', 'architecture'),
(4, 'PostgreSQL', 'postgresql')
ON CONFLICT (id) DO NOTHING;

SELECT setval('blog_tags_id_seq', (SELECT MAX(id) FROM blog_tags));

-- 11. Sample Blog Post
INSERT INTO blogs (id, title, slug, excerpt, content, cover_image_url, author, category_id, status, published_at, reading_time_minutes, seo_title, seo_description, seo_keywords)
VALUES
(
    1,
    'Building Resilient Microservices with Java 21 Virtual Threads & Spring Boot 3',
    'building-resilient-microservices-java-21-spring-boot-3',
    'A deep technical look into how Project Loom virtual threads revolutionize synchronous I/O throughput in modern Spring Boot enterprise services.',
    '# Building Resilient Microservices with Java 21 & Spring Boot 3

In high-concurrency backend services, traditional thread-per-request models often lead to memory saturation and thread starvation when waiting for network I/O or database responses.

With the release of **Java 21 LTS** and **Project Loom virtual threads**, developers can now achieve reactive-style throughput while maintaining standard synchronous, sequential code readability.

## Enabling Virtual Threads in Spring Boot 3

In Spring Boot 3.2+, enabling virtual threads is as simple as adding a single configuration flag:

```yaml
spring:
  threads:
    virtual:
      enabled: true
```

Under the hood, Tomcat and Spring MVC dispatch incoming HTTP requests onto lightweight virtual threads mounted onto a small carrier pool of OS threads.

## Database Connection Pool Considerations

When switching to virtual threads, standard HikariCP connection pools still govern max concurrent database queries. Ensuring that pool sizing matches database capacity prevents queue bloat.

## Conclusion

Virtual threads offer an unparalleled balance between developer ergonomics and astronomical I/O throughput.',
    'https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=1200&q=80',
    'Shivam S.',
    1,
    'PUBLISHED',
    CURRENT_TIMESTAMP,
    5,
    'Building Resilient Microservices with Java 21 | Technical Guide',
    'Discover how Java 21 virtual threads and Spring Boot 3 provide high throughput and clean synchronous code.',
    'java, spring boot, virtual threads, microservices, architecture'
)
ON CONFLICT (id) DO NOTHING;

SELECT setval('blogs_id_seq', (SELECT MAX(id) FROM blogs));

INSERT INTO blog_post_tags (blog_id, tag_id) VALUES
(1, 1), (1, 2), (1, 3)
ON CONFLICT DO NOTHING;

-- 12. Social Links
INSERT INTO social_links (platform, url, icon, display_order, active) VALUES
('GitHub', 'https://github.com', 'github', 1, TRUE),
('LinkedIn', 'https://linkedin.com', 'linkedin', 2, TRUE),
('Twitter', 'https://twitter.com', 'twitter', 3, TRUE),
('YouTube', 'https://youtube.com', 'youtube', 4, TRUE);

-- 13. Site Settings
INSERT INTO site_settings (site_name, site_description, logo_url, favicon_url, contact_email, default_seo_title, default_seo_description, resume_url)
VALUES
(
    'Shivam | Senior Software Architect & Full-Stack Engineer',
    'Portfolio and technical insights of a senior full-stack software engineer specializing in Java, Spring Boot, PostgreSQL, and Next.js.',
    'https://portfolio-assets.com/logo.svg',
    'https://portfolio-assets.com/favicon.ico',
    'shivam@developer.io',
    'Shivam - Senior Full-Stack Engineer & Software Architect',
    'Personal portfolio showcasing production-grade distributed systems, cloud applications, and software architecture.',
    'https://portfolio-assets.com/docs/resume.pdf'
) ON CONFLICT DO NOTHING;
