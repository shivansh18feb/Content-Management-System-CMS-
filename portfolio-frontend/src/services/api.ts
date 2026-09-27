import axios from 'axios';
import {
  ApiResponse,
  PagedResponse,
  About,
  ProjectSummary,
  ProjectDetail,
  SkillGroup,
  Experience,
  Education,
  ServiceOffering,
  Testimonial,
  BlogSummary,
  BlogDetail,
  SocialLink,
  SiteSettings
} from '../types';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080';

export const publicApi = {
  getAbout: async () => {
    const res = await axios.get<ApiResponse<About>>(`${API_BASE}/api/public/about`);
    return res.data.data;
  },

  getFeaturedProjects: async () => {
    const res = await axios.get<ApiResponse<ProjectSummary[]>>(`${API_BASE}/api/public/projects/featured`);
    return res.data.data;
  },

  getProjects: async (params?: { search?: string; category?: string; page?: number; size?: number }) => {
    const res = await axios.get<ApiResponse<PagedResponse<ProjectSummary>>>(`${API_BASE}/api/public/projects`, { params });
    return res.data.data;
  },

  getProjectBySlug: async (slug: string) => {
    const res = await axios.get<ApiResponse<ProjectDetail>>(`${API_BASE}/api/public/projects/${slug}`);
    return res.data.data;
  },

  getSkills: async () => {
    const res = await axios.get<ApiResponse<SkillGroup[]>>(`${API_BASE}/api/public/skills`);
    return res.data.data;
  },

  getExperience: async () => {
    const res = await axios.get<ApiResponse<Experience[]>>(`${API_BASE}/api/public/experience`);
    return res.data.data;
  },

  getEducation: async () => {
    const res = await axios.get<ApiResponse<Education[]>>(`${API_BASE}/api/public/education`);
    return res.data.data;
  },

  getServices: async () => {
    const res = await axios.get<ApiResponse<ServiceOffering[]>>(`${API_BASE}/api/public/services`);
    return res.data.data;
  },

  getTestimonials: async () => {
    const res = await axios.get<ApiResponse<Testimonial[]>>(`${API_BASE}/api/public/testimonials`);
    return res.data.data;
  },

  getBlogs: async (params?: { search?: string; category?: string; tag?: string; page?: number; size?: number }) => {
    const res = await axios.get<ApiResponse<PagedResponse<BlogSummary>>>(`${API_BASE}/api/public/blogs`, { params });
    return res.data.data;
  },

  getBlogBySlug: async (slug: string) => {
    const res = await axios.get<ApiResponse<BlogDetail>>(`${API_BASE}/api/public/blogs/${slug}`);
    return res.data.data;
  },

  getSocialLinks: async () => {
    const res = await axios.get<ApiResponse<SocialLink[]>>(`${API_BASE}/api/public/social-links`);
    return res.data.data;
  },

  getSettings: async () => {
    const res = await axios.get<ApiResponse<SiteSettings>>(`${API_BASE}/api/public/settings`);
    return res.data.data;
  },

  submitContact: async (payload: { name: string; email: string; subject: string; message: string }) => {
    const res = await axios.post<ApiResponse<any>>(`${API_BASE}/api/public/contact`, payload);
    return res.data;
  },
};
