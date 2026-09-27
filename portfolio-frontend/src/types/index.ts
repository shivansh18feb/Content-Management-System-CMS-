export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
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

export interface About {
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
  status: string;
  displayOrder: number;
  startDate?: string;
  endDate?: string;
}

export interface ProjectDetail extends ProjectSummary {
  description: string;
  seoTitle?: string;
  seoDescription?: string;
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
}

export interface SkillGroup {
  category: SkillCategory;
  skills: Skill[];
}

export interface Experience {
  id: number;
  company: string;
  position: string;
  description: string;
  location?: string;
  employmentType: string;
  startDate: string;
  endDate?: string;
  current: boolean;
  technologies?: string;
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
}

export interface ServiceOffering {
  id: number;
  title: string;
  description: string;
  icon?: string;
  features: string[];
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
}

export interface BlogSummary {
  id: number;
  title: string;
  slug: string;
  excerpt: string;
  coverImageUrl?: string;
  author: string;
  tags: { id: number; name: string; slug: string }[];
  readingTimeMinutes: number;
  publishedAt?: string;
}

export interface BlogDetail extends BlogSummary {
  content: string;
  seoTitle?: string;
  seoDescription?: string;
}

export interface SocialLink {
  id: number;
  platform: string;
  url: string;
  icon: string;
}

export interface SiteSettings {
  siteName: string;
  siteDescription?: string;
  logoUrl?: string;
  faviconUrl?: string;
  contactEmail?: string;
  defaultSeoTitle?: string;
  defaultSeoDescription?: string;
  resumeUrl?: string;
}
