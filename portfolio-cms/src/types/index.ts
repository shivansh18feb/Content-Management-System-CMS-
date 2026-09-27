export interface User {
  id: number;
  email: string;
  fullName: string;
  role: 'ADMIN' | 'EDITOR';
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errors?: Record<string, string>;
  timestamp: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface DashboardStats {
  totalProjects: number;
  publishedProjects: number;
  draftProjects: number;
  archivedProjects: number;
  totalBlogs: number;
  publishedBlogs: number;
  draftBlogs: number;
  totalSkills: number;
  totalSkillCategories: number;
  totalExperiences: number;
  totalEducations: number;
  totalServices: number;
  totalTestimonials: number;
  publishedTestimonials: number;
  totalMessages: number;
  unreadMessages: number;
  totalMediaFiles: number;
  totalSocialLinks: number;
  recentActivity: {
    id: number;
    action: string;
    entityType: string;
    entityId: string;
    userEmail: string;
    createdAt: string;
  }[];
}

export interface ProjectSummary {
  id: number;
  title: string;
  slug: string;
  shortDescription: string;
  thumbnailUrl?: string;
  githubUrl?: string;
  liveUrl?: string;
  documentationUrl?: string;
  technologies: string[];
  category: string;
  featured: boolean;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  displayOrder: number;
  startDate?: string;
  endDate?: string;
}

export interface ProjectDetail extends ProjectSummary {
  description: string;
  seoTitle?: string;
  seoDescription?: string;
  createdAt: string;
  updatedAt: string;
}

export interface BlogSummary {
  id: number;
  title: string;
  slug: string;
  excerpt: string;
  coverImageUrl?: string;
  author: string;
  category?: { id: number; name: string; slug: string };
  tags: { id: number; name: string; slug: string }[];
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
  publishedAt?: string;
  readingTimeMinutes: number;
  createdAt: string;
}

export interface BlogDetail extends BlogSummary {
  content: string;
  seoTitle?: string;
  seoDescription?: string;
  seoKeywords?: string;
  updatedAt: string;
}

export interface SkillCategory {
  id: number;
  name: string;
  displayOrder: number;
}

export interface Skill {
  id: number;
  name: string;
  icon?: string;
  proficiency: number;
  yearsOfExperience?: number;
  category?: SkillCategory;
  displayOrder: number;
  featured: boolean;
  active: boolean;
}

export interface Experience {
  id: number;
  company: string;
  position: string;
  description: string;
  location?: string;
  employmentType: 'FULL_TIME' | 'PART_TIME' | 'CONTRACT' | 'FREELANCE' | 'INTERNSHIP';
  startDate: string;
  endDate?: string;
  current: boolean;
  technologies?: string;
  displayOrder: number;
}

export interface Education {
  id: number;
  institution: string;
  degree: string;
  fieldOfStudy: string;
  description?: string;
  startDate: string;
  endDate?: string;
  grade?: string;
  location?: string;
  displayOrder: number;
}

export interface ServiceOffering {
  id: number;
  title: string;
  description: string;
  icon?: string;
  features: string[];
  displayOrder: number;
  active: boolean;
}

export interface Testimonial {
  id: number;
  name: string;
  role: string;
  company?: string;
  avatarUrl?: string;
  content: string;
  rating: number;
  featured: boolean;
  published: boolean;
  displayOrder: number;
}

export interface MediaFile {
  id: number;
  originalFilename: string;
  storedFilename: string;
  filePath: string;
  fileUrl: string;
  contentType: string;
  fileSize: number;
  mediaType: 'IMAGE' | 'DOCUMENT' | 'VIDEO' | 'AUDIO' | 'OTHER';
  altText?: string;
  createdAt: string;
  createdBy?: string;
}

export interface ContactMessage {
  id: number;
  name: string;
  email: string;
  subject: string;
  message: string;
  status: 'UNREAD' | 'READ' | 'ARCHIVED' | 'SPAM';
  ipAddress?: string;
  createdAt: string;
}

export interface SocialLink {
  id: number;
  platform: string;
  url: string;
  icon: string;
  displayOrder: number;
  active: boolean;
}

export interface SiteSettings {
  id: number;
  siteName: string;
  siteDescription?: string;
  logoUrl?: string;
  faviconUrl?: string;
  contactEmail?: string;
  defaultSeoTitle?: string;
  defaultSeoDescription?: string;
  defaultSeoImage?: string;
  resumeUrl?: string;
}

export interface About {
  id: number;
  headline: string;
  shortBio: string;
  longBio: string;
  profileImageUrl?: string;
  location?: string;
  email?: string;
  phone?: string;
  resumeUrl?: string;
  availability: string;
  yearsOfExperience: number;
  githubUrl?: string;
  linkedinUrl?: string;
  twitterUrl?: string;
}

export interface AuditLog {
  id: number;
  userId?: number;
  userEmail?: string;
  action: string;
  entityType: string;
  entityId?: string;
  ipAddress?: string;
  metadata?: string;
  createdAt: string;
}
