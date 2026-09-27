import React, { useEffect, useState } from 'react';
import {
  ArrowRight,
  Download,
  ExternalLink,
  Mail,
  MapPin,
  CheckCircle2,
  Star,
  Sparkles,
  Send,
  Code2,
  FileCode2,
  Check
} from 'lucide-react';
import { publicApi } from '../services/api';
import {
  About,
  ProjectSummary,
  SkillGroup,
  Experience,
  Education,
  ServiceOffering,
  Testimonial,
  BlogSummary,
  SocialLink,
  SiteSettings
} from '../types';
import { Navbar } from '../components/Navbar';
import { Footer } from '../components/Footer';
import { ProjectModal } from '../components/ProjectModal';
import { BlogModal } from '../components/BlogModal';

export const Home: React.FC = () => {
  // Data states
  const [about, setAbout] = useState<About | null>(null);
  const [projects, setProjects] = useState<ProjectSummary[]>([]);
  const [skillGroups, setSkillGroups] = useState<SkillGroup[]>([]);
  const [experiences, setExperiences] = useState<Experience[]>([]);
  const [educations, setEducations] = useState<Education[]>([]);
  const [services, setServices] = useState<ServiceOffering[]>([]);
  const [testimonials, setTestimonials] = useState<Testimonial[]>([]);
  const [blogs, setBlogs] = useState<BlogSummary[]>([]);
  const [socialLinks, setSocialLinks] = useState<SocialLink[]>([]);
  const [settings, setSettings] = useState<SiteSettings | null>(null);

  // UI state
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [activeProjectSlug, setActiveProjectSlug] = useState<string | null>(null);
  const [activeBlogSlug, setActiveBlogSlug] = useState<string | null>(null);

  // Contact form state
  const [contactForm, setContactForm] = useState({
    name: '',
    email: '',
    subject: '',
    message: '',
  });
  const [sendingContact, setSendingContact] = useState(false);
  const [contactStatus, setContactStatus] = useState<{ type: 'success' | 'error'; message: string } | null>(null);

  useEffect(() => {
    fetchAllData();
  }, []);

  const fetchAllData = async () => {
    try {
      const [
        aboutData,
        projectsData,
        skillsData,
        expData,
        eduData,
        servicesData,
        testimonialsData,
        blogsData,
        socialData,
        settingsData
      ] = await Promise.all([
        publicApi.getAbout().catch(() => null),
        publicApi.getProjects({ size: 12 }).then((r) => r.content).catch(() => []),
        publicApi.getSkills().catch(() => []),
        publicApi.getExperience().catch(() => []),
        publicApi.getEducation().catch(() => []),
        publicApi.getServices().catch(() => []),
        publicApi.getTestimonials().catch(() => []),
        publicApi.getBlogs({ size: 6 }).then((r) => r.content).catch(() => []),
        publicApi.getSocialLinks().catch(() => []),
        publicApi.getSettings().catch(() => null),
      ]);

      if (aboutData) setAbout(aboutData);
      setProjects(projectsData);
      setSkillGroups(skillsData);
      setExperiences(expData);
      setEducations(eduData);
      setServices(servicesData);
      setTestimonials(testimonialsData);
      setBlogs(blogsData);
      setSocialLinks(socialData);
      if (settingsData) setSettings(settingsData);
    } catch (err) {
      console.error('Error bootstrapping portfolio content', err);
    }
  };

  const handleContactSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSendingContact(true);
    setContactStatus(null);

    try {
      const res = await publicApi.submitContact(contactForm);
      if (res.success) {
        setContactStatus({
          type: 'success',
          message: 'Thank you! Your message has been received and notified to the administrator.',
        });
        setContactForm({ name: '', email: '', subject: '', message: '' });
      } else {
        setContactStatus({ type: 'error', message: res.message || 'Submission failed.' });
      }
    } catch (err: any) {
      setContactStatus({
        type: 'error',
        message: err.response?.data?.message || 'Failed to deliver message. Please try again.',
      });
    } finally {
      setSendingContact(false);
    }
  };

  // Unique categories for filtering
  const categories = ['ALL', ...Array.from(new Set(projects.map((p) => p.category)))];
  const filteredProjects =
    selectedCategory === 'ALL'
      ? projects
      : projects.filter((p) => p.category === selectedCategory);

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col selection:bg-indigo-500 selection:text-white">
      <Navbar siteName={settings?.siteName || 'Portfolio'} />

      {/* Hero Section */}
      <section className="relative pt-36 pb-20 md:pt-44 md:pb-28 px-4 sm:px-6 lg:px-8 overflow-hidden">
        {/* Ambient Gradient Glows */}
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 w-[600px] h-[350px] bg-indigo-600/15 rounded-full blur-[140px] pointer-events-none"></div>
        <div className="absolute top-1/3 right-10 w-[400px] h-[250px] bg-purple-600/10 rounded-full blur-[120px] pointer-events-none"></div>

        <div className="max-w-5xl mx-auto text-center space-y-6 relative z-10">
          {/* Availability Badge */}
          <div className="inline-flex items-center space-x-2 px-3.5 py-1.5 rounded-full glass text-xs font-medium text-emerald-400 border border-emerald-500/20 shadow-lg">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
            <span>{about?.availability === 'AVAILABLE' ? 'Available for new engineering projects' : 'Open to compelling opportunities'}</span>
          </div>

          <h1 className="text-4xl sm:text-6xl md:text-7xl font-extrabold tracking-tight text-white leading-[1.1]">
            Building resilient systems,{' '}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 via-purple-400 to-pink-400">
              at enterprise scale.
            </span>
          </h1>

          <p className="max-w-2xl mx-auto text-base sm:text-lg text-slate-400 leading-relaxed font-normal">
            {about?.headline || 'Senior Full-Stack Engineer & Distributed Systems Architect'}
          </p>
          <p className="max-w-xl mx-auto text-sm text-slate-500">
            {about?.shortBio || 'Specializing in Java 21, Spring Boot microservices, high-throughput pipelines, and cloud native architectures.'}
          </p>

          {/* Call to Actions */}
          <div className="flex flex-wrap justify-center gap-4 pt-4">
            <a
              href="#projects"
              className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium px-6 py-3 rounded-xl transition shadow-lg shadow-indigo-600/30 text-sm"
            >
              <span>Explore Projects</span>
              <ArrowRight className="w-4 h-4" />
            </a>

            {about?.resumeUrl && (
              <a
                href={about.resumeUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="flex items-center space-x-2 bg-slate-900 hover:bg-slate-800 text-slate-200 font-medium px-6 py-3 rounded-xl border border-slate-800 transition text-sm"
              >
                <Download className="w-4 h-4 text-slate-400" />
                <span>Resume / CV</span>
              </a>
            )}

            <a
              href="#contact"
              className="flex items-center space-x-2 bg-slate-900/60 hover:bg-slate-800 text-slate-300 font-medium px-6 py-3 rounded-xl border border-slate-800 transition text-sm"
            >
              <Mail className="w-4 h-4 text-indigo-400" />
              <span>Contact Me</span>
            </a>
          </div>

          {/* Quick Metrics Bar */}
          <div className="pt-12 grid grid-cols-2 sm:grid-cols-4 gap-4 max-w-3xl mx-auto">
            <div className="glass p-4 rounded-2xl">
              <span className="text-2xl font-bold text-white">{about?.yearsOfExperience || 6}+</span>
              <p className="text-xs text-slate-400 mt-0.5">Years Experience</p>
            </div>
            <div className="glass p-4 rounded-2xl">
              <span className="text-2xl font-bold text-white">{projects.length}+</span>
              <p className="text-xs text-slate-400 mt-0.5">Production Projects</p>
            </div>
            <div className="glass p-4 rounded-2xl">
              <span className="text-2xl font-bold text-white">99.9%</span>
              <p className="text-xs text-slate-400 mt-0.5">Uptime Architecture</p>
            </div>
            <div className="glass p-4 rounded-2xl">
              <span className="text-2xl font-bold text-white">{experiences.length}</span>
              <p className="text-xs text-slate-400 mt-0.5">Enterprise Roles</p>
            </div>
          </div>
        </div>
      </section>

      {/* About Section */}
      <section id="about" className="py-20 px-4 sm:px-6 lg:px-8 border-t border-slate-900 bg-slate-950/40">
        <div className="max-w-5xl mx-auto space-y-10">
          <div className="text-center space-y-2">
            <span className="text-xs font-semibold uppercase tracking-wider text-indigo-400">Background</span>
            <h2 className="text-3xl font-bold text-white">Engineering Philosophy</h2>
          </div>

          <div className="glass-card p-8 rounded-3xl border border-slate-800/80 space-y-6">
            <div className="flex flex-col md:flex-row gap-8 items-start">
              <div className="space-y-4 flex-1 text-slate-300 text-sm leading-relaxed whitespace-pre-line">
                {about?.longBio ||
                  'I design and build high-performance software systems with a strong emphasis on clean domain modeling, distributed data consistency, and developer ergonomic velocity.'}
              </div>

              <div className="w-full md:w-72 bg-slate-900/80 p-5 rounded-2xl border border-slate-800 space-y-4 shrink-0 text-xs">
                <div>
                  <span className="text-slate-500 font-semibold uppercase tracking-wider">Location</span>
                  <p className="text-slate-200 mt-0.5 flex items-center gap-1.5 font-medium">
                    <MapPin className="w-3.5 h-3.5 text-indigo-400" />
                    {about?.location || 'San Francisco, CA / Remote'}
                  </p>
                </div>
                <div>
                  <span className="text-slate-500 font-semibold uppercase tracking-wider">Primary Stack</span>
                  <p className="text-slate-200 mt-0.5 font-medium">Java 21, Spring Boot 3, React, Postgres, Docker</p>
                </div>
                <div>
                  <span className="text-slate-500 font-semibold uppercase tracking-wider">Email</span>
                  <p className="text-slate-200 mt-0.5 font-mono">{about?.email || 'shivam@example.com'}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Projects Section */}
      <section id="projects" className="py-20 px-4 sm:px-6 lg:px-8 border-t border-slate-900">
        <div className="max-w-7xl mx-auto space-y-10">
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end gap-4">
            <div>
              <span className="text-xs font-semibold uppercase tracking-wider text-indigo-400">Portfolio</span>
              <h2 className="text-3xl font-bold text-white mt-1">Featured Case Studies</h2>
              <p className="text-sm text-slate-400 mt-1">Production architectures, microservices, and distributed applications</p>
            </div>

            {/* Category filter tabs */}
            <div className="flex flex-wrap gap-1.5 bg-slate-900 p-1 rounded-xl border border-slate-800 text-xs">
              {categories.map((cat) => (
                <button
                  key={cat}
                  onClick={() => setSelectedCategory(cat)}
                  className={`px-3 py-1.5 rounded-lg transition font-medium ${
                    selectedCategory === cat
                      ? 'bg-indigo-600 text-white shadow-md'
                      : 'text-slate-400 hover:text-slate-200'
                  }`}
                >
                  {cat}
                </button>
              ))}
            </div>
          </div>

          {/* Projects Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredProjects.map((p) => (
              <div
                key={p.id}
                className="glass-card rounded-2xl overflow-hidden border border-slate-800 hover:border-slate-700 transition duration-300 flex flex-col justify-between group"
              >
                <div>
                  {/* Thumbnail / Header */}
                  <div className="h-48 bg-slate-900 overflow-hidden relative">
                    {p.thumbnailUrl ? (
                      <img
                        src={p.thumbnailUrl}
                        alt={p.title}
                        className="w-full h-full object-cover group-hover:scale-105 transition duration-500"
                        onError={(e) => {
                          (e.target as any).src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="400" height="200"><rect width="100%" height="100%" fill="%230f172a"/><text x="50%" y="50%" fill="%23475569" font-family="sans-serif" font-size="14" text-anchor="middle">Project Architecture</text></svg>';
                        }}
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center bg-slate-900">
                        <FileCode2 className="w-12 h-12 text-slate-700" />
                      </div>
                    )}
                    <span className="absolute top-3 left-3 text-[10px] uppercase font-bold tracking-wider px-2.5 py-1 rounded-full bg-slate-950/80 text-indigo-400 border border-slate-800 backdrop-blur-sm">
                      {p.category}
                    </span>
                  </div>

                  <div className="p-5 space-y-3">
                    <h3 className="font-bold text-lg text-white group-hover:text-indigo-400 transition">
                      {p.title}
                    </h3>
                    <p className="text-xs text-slate-400 line-clamp-2 leading-relaxed">
                      {p.shortDescription}
                    </p>

                    <div className="flex flex-wrap gap-1 pt-1">
                      {p.technologies?.slice(0, 4).map((tech, idx) => (
                        <span
                          key={idx}
                          className="text-[10px] bg-slate-900 text-slate-300 px-2 py-0.5 rounded border border-slate-800 font-mono"
                        >
                          {tech}
                        </span>
                      ))}
                      {p.technologies?.length > 4 && (
                        <span className="text-[10px] text-slate-500 font-mono">+{p.technologies.length - 4}</span>
                      )}
                    </div>
                  </div>
                </div>

                <div className="p-5 pt-0 border-t border-slate-900 flex justify-between items-center text-xs mt-4">
                  <button
                    onClick={() => setActiveProjectSlug(p.slug)}
                    className="text-indigo-400 hover:text-indigo-300 font-semibold flex items-center gap-1"
                  >
                    <span>Deep Case Study</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </button>

                  <div className="flex space-x-2">
                    {p.githubUrl && (
                      <a
                        href={p.githubUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="p-1.5 text-slate-400 hover:text-white"
                        title="GitHub"
                      >
                        <Code2 className="w-4 h-4" />
                      </a>
                    )}
                    {p.liveUrl && (
                      <a
                        href={p.liveUrl}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="p-1.5 text-slate-400 hover:text-white"
                        title="Live Site"
                      >
                        <ExternalLink className="w-4 h-4" />
                      </a>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Technical Skills Section */}
      <section id="skills" className="py-20 px-4 sm:px-6 lg:px-8 border-t border-slate-900 bg-slate-950/60">
        <div className="max-w-5xl mx-auto space-y-10">
          <div className="text-center space-y-2">
            <span className="text-xs font-semibold uppercase tracking-wider text-indigo-400">Competencies</span>
            <h2 className="text-3xl font-bold text-white">Technical Arsenal</h2>
            <p className="text-sm text-slate-400 max-w-lg mx-auto">Core frameworks, cloud orchestration, databases, and programming languages</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {skillGroups.map((group) => (
              <div key={group.category.id} className="glass-card p-6 rounded-2xl border border-slate-800 space-y-4">
                <h3 className="font-bold text-white text-base border-b border-slate-800 pb-3 flex items-center justify-between">
                  <span>{group.category.name}</span>
                  <span className="text-xs font-mono text-indigo-400 font-normal">{group.skills.length} tools</span>
                </h3>

                <div className="space-y-3">
                  {group.skills.map((skill) => (
                    <div key={skill.id} className="space-y-1">
                      <div className="flex justify-between items-center text-xs">
                        <span className="font-semibold text-slate-200">{skill.name}</span>
                        <div className="flex items-center gap-2">
                          {skill.yearsOfExperience && (
                            <span className="text-[10px] text-slate-500">{skill.yearsOfExperience} yrs</span>
                          )}
                          <span className="font-mono text-indigo-400 font-medium">{skill.proficiency}%</span>
                        </div>
                      </div>
                      <div className="w-full bg-slate-900 h-1.5 rounded-full overflow-hidden border border-slate-800">
                        <div
                          className="bg-gradient-to-r from-indigo-500 to-purple-500 h-full rounded-full transition-all duration-700"
                          style={{ width: `${skill.proficiency}%` }}
                        />
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Experience & Career Journey */}
      <section id="experience" className="py-20 px-4 sm:px-6 lg:px-8 border-t border-slate-900">
        <div className="max-w-4xl mx-auto space-y-10">
          <div className="text-center space-y-2">
            <span className="text-xs font-semibold uppercase tracking-wider text-indigo-400">Career Trajectory</span>
            <h2 className="text-3xl font-bold text-white">Work Experience</h2>
          </div>

          <div className="relative border-l-2 border-slate-800 ml-4 md:ml-6 space-y-8">
            {experiences.map((exp) => (
              <div key={exp.id} className="relative pl-6 md:pl-8 group">
                {/* Node icon */}
                <div className="absolute -left-[9px] top-1.5 w-4 h-4 rounded-full bg-slate-950 border-2 border-indigo-500 group-hover:scale-125 transition"></div>

                <div className="glass-card p-6 rounded-2xl border border-slate-800 hover:border-slate-700 transition space-y-3">
                  <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-1">
                    <div>
                      <h3 className="font-bold text-white text-lg">{exp.position}</h3>
                      <span className="text-sm font-semibold text-indigo-400">{exp.company}</span>
                    </div>
                    <span className="text-xs font-mono text-slate-400 bg-slate-900 px-2.5 py-1 rounded-md border border-slate-800 self-start sm:self-auto">
                      {exp.startDate} - {exp.current ? 'Present' : exp.endDate || 'N/A'}
                    </span>
                  </div>

                  <p className="text-xs text-slate-300 leading-relaxed whitespace-pre-line">
                    {exp.description}
                  </p>

                  {exp.technologies && (
                    <div className="flex flex-wrap gap-1 pt-2">
                      {exp.technologies.split(',').map((tech, idx) => (
                        <span key={idx} className="text-[10px] bg-slate-950 text-slate-400 border border-slate-800 px-2 py-0.5 rounded font-mono">
                          {tech.trim()}
                        </span>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Education Section */}
      {educations.length > 0 && (
        <section id="education" className="py-20 px-4 sm:px-6 lg:px-8 border-t border-slate-900 bg-slate-950/40">
          <div className="max-w-4xl mx-auto space-y-10">
            <div className="text-center space-y-2">
              <span className="text-xs font-semibold uppercase tracking-wider text-indigo-400">Academic Background</span>
              <h2 className="text-3xl font-bold text-white">Education</h2>
            </div>

            <div className="relative border-l-2 border-slate-800 ml-4 md:ml-6 space-y-8">
              {educations.map((edu) => (
                <div key={edu.id} className="relative pl-6 md:pl-8 group">
                  {/* Node icon */}
                  <div className="absolute -left-[9px] top-1.5 w-4 h-4 rounded-full bg-slate-950 border-2 border-violet-500 group-hover:scale-125 transition"></div>

                  <div className="glass-card p-6 rounded-2xl border border-slate-800 hover:border-slate-700 transition space-y-3">
                    <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-1">
                      <div>
                        <h3 className="font-bold text-white text-lg">{edu.degree}</h3>
                        <span className="text-sm font-semibold text-violet-400">{edu.institution}</span>
                        {edu.fieldOfStudy && (
                          <p className="text-xs text-slate-400 mt-0.5">{edu.fieldOfStudy}</p>
                        )}
                      </div>
                      <div className="flex flex-col items-start sm:items-end gap-1">
                        <span className="text-xs font-mono text-slate-400 bg-slate-900 px-2.5 py-1 rounded-md border border-slate-800 self-start sm:self-auto">
                          {edu.startDate}{edu.endDate ? ` - ${edu.endDate}` : ' - Present'}
                        </span>
                        {edu.grade && (
                          <span className="text-xs font-semibold text-emerald-400 bg-emerald-950/40 border border-emerald-900/50 px-2 py-0.5 rounded">
                            GPA / Grade: {edu.grade}
                          </span>
                        )}
                      </div>
                    </div>

                    {edu.description && (
                      <p className="text-xs text-slate-300 leading-relaxed whitespace-pre-line">
                        {edu.description}
                      </p>
                    )}

                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* Services Section */}
      {services.length > 0 && (
        <section id="services" className="py-20 px-4 sm:px-6 lg:px-8 border-t border-slate-900 bg-slate-950/40">
          <div className="max-w-6xl mx-auto space-y-10">
            <div className="text-center space-y-2">
              <span className="text-xs font-semibold uppercase tracking-wider text-indigo-400">Offerings</span>
              <h2 className="text-3xl font-bold text-white">Architectural Capabilities & Services</h2>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              {services.map((svc) => (
                <div key={svc.id} className="glass-card p-6 rounded-2xl border border-slate-800 space-y-4 flex flex-col justify-between">
                  <div className="space-y-3">
                    <div className="w-10 h-10 rounded-xl bg-indigo-600/10 border border-indigo-500/20 flex items-center justify-center text-indigo-400">
                      <Sparkles className="w-5 h-5" />
                    </div>
                    <h3 className="font-bold text-lg text-white">{svc.title}</h3>
                    <p className="text-xs text-slate-400 leading-relaxed">{svc.description}</p>
                  </div>

                  <div className="space-y-2 pt-4 border-t border-slate-800/80">
                    {svc.features?.map((f, idx) => (
                      <div key={idx} className="flex items-center text-xs text-slate-300 gap-2">
                        <Check className="w-3.5 h-3.5 text-emerald-400 shrink-0" />
                        <span>{f}</span>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* Testimonials */}
      {testimonials.length > 0 && (
        <section id="testimonials" className="py-20 px-4 sm:px-6 lg:px-8 border-t border-slate-900">
          <div className="max-w-5xl mx-auto space-y-10">
            <div className="text-center space-y-2">
              <span className="text-xs font-semibold uppercase tracking-wider text-indigo-400">Endorsements</span>
              <h2 className="text-3xl font-bold text-white">Client & Colleague Recommendations</h2>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {testimonials.map((t) => (
                <div key={t.id} className="glass-card p-6 rounded-2xl border border-slate-800 space-y-4">
                  <div className="flex items-center space-x-3">
                    <div className="w-10 h-10 rounded-full bg-slate-800 flex items-center justify-center font-bold text-indigo-400">
                      {t.name.charAt(0)}
                    </div>
                    <div>
                      <h4 className="font-bold text-white text-sm">{t.name}</h4>
                      <p className="text-xs text-slate-400">{t.role}{t.company ? ` • ${t.company}` : ''}</p>
                    </div>
                  </div>

                  <div className="flex">
                    {Array.from({ length: t.rating }).map((_, i) => (
                      <Star key={i} className="w-3.5 h-3.5 text-amber-400 fill-amber-400" />
                    ))}
                  </div>

                  <p className="text-xs text-slate-300 italic leading-relaxed">
                    "{t.content}"
                  </p>
                </div>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* Blog Articles Section */}
      {blogs.length > 0 && (
        <section id="blog" className="py-20 px-4 sm:px-6 lg:px-8 border-t border-slate-900 bg-slate-950/40">
          <div className="max-w-6xl mx-auto space-y-10">
            <div className="text-center space-y-2">
              <span className="text-xs font-semibold uppercase tracking-wider text-indigo-400">Insights</span>
              <h2 className="text-3xl font-bold text-white">Technical Deep Dives</h2>
              <p className="text-sm text-slate-400">Engineering writeups, best practices, and architecture tutorials</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {blogs.map((b) => (
                <div
                  key={b.id}
                  onClick={() => setActiveBlogSlug(b.slug)}
                  className="glass-card p-6 rounded-2xl border border-slate-800 hover:border-slate-700 transition cursor-pointer flex flex-col justify-between group"
                >
                  <div className="space-y-3">
                    <div className="flex flex-wrap gap-1.5">
                      {b.tags?.map((t) => (
                        <span key={t.id} className="text-[10px] text-indigo-400 font-mono">
                          #{t.name}
                        </span>
                      ))}
                    </div>

                    <h3 className="font-bold text-lg text-white group-hover:text-indigo-400 transition">
                      {b.title}
                    </h3>
                    <p className="text-xs text-slate-400 line-clamp-3 leading-relaxed">
                      {b.excerpt}
                    </p>
                  </div>

                  <div className="pt-4 border-t border-slate-900 flex justify-between items-center text-xs text-slate-500 mt-4">
                    <span>{b.readingTimeMinutes} min read</span>
                    <span className="text-indigo-400 font-medium flex items-center gap-1 group-hover:translate-x-1 transition">
                      Read Article →
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* Contact Form Section */}
      <section id="contact" className="py-20 px-4 sm:px-6 lg:px-8 border-t border-slate-900">
        <div className="max-w-2xl mx-auto space-y-8">
          <div className="text-center space-y-2">
            <span className="text-xs font-semibold uppercase tracking-wider text-indigo-400">Get In Touch</span>
            <h2 className="text-3xl font-bold text-white">Let's Build Something Exceptional</h2>
            <p className="text-sm text-slate-400">Send an inquiry directly to my inbox and CMS dashboard.</p>
          </div>

          <form onSubmit={handleContactSubmit} className="glass-card p-6 sm:p-8 rounded-3xl border border-slate-800 space-y-4 shadow-2xl">
            {contactStatus && (
              <div
                className={`p-4 rounded-xl text-xs flex items-center gap-2 ${
                  contactStatus.type === 'success'
                    ? 'bg-emerald-500/10 border border-emerald-500/20 text-emerald-400'
                    : 'bg-red-500/10 border border-red-500/20 text-red-400'
                }`}
              >
                {contactStatus.type === 'success' ? <CheckCircle2 className="w-4 h-4 shrink-0" /> : null}
                <span>{contactStatus.message}</span>
              </div>
            )}

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Your Name *</label>
                <input
                  type="text"
                  required
                  value={contactForm.name}
                  onChange={(e) => setContactForm({ ...contactForm, name: e.target.value })}
                  placeholder="John Doe"
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3.5 py-2.5 text-sm text-white placeholder-slate-600 focus:outline-none focus:border-indigo-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Your Email *</label>
                <input
                  type="email"
                  required
                  value={contactForm.email}
                  onChange={(e) => setContactForm({ ...contactForm, email: e.target.value })}
                  placeholder="john@example.com"
                  className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3.5 py-2.5 text-sm text-white placeholder-slate-600 focus:outline-none focus:border-indigo-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Subject *</label>
              <input
                type="text"
                required
                value={contactForm.subject}
                onChange={(e) => setContactForm({ ...contactForm, subject: e.target.value })}
                placeholder="Project Inquiry / Tech Consultation"
                className="w-full bg-slate-950 border border-slate-800 rounded-xl px-3.5 py-2.5 text-sm text-white placeholder-slate-600 focus:outline-none focus:border-indigo-500"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-300 mb-1">Message *</label>
              <textarea
                required
                rows={4}
                value={contactForm.message}
                onChange={(e) => setContactForm({ ...contactForm, message: e.target.value })}
                placeholder="Describe your requirements, timeline, or engineering opportunity..."
                className="w-full bg-slate-950 border border-slate-800 rounded-xl p-3 text-sm text-white placeholder-slate-600 focus:outline-none focus:border-indigo-500"
              />
            </div>

            <button
              type="submit"
              disabled={sendingContact}
              className="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-medium py-3 rounded-xl transition duration-150 flex items-center justify-center space-x-2 text-sm shadow-lg shadow-indigo-600/30 disabled:opacity-50"
            >
              <Send className="w-4 h-4" />
              <span>{sendingContact ? 'Delivering message...' : 'Send Message'}</span>
            </button>
          </form>
        </div>
      </section>

      <Footer socialLinks={socialLinks} siteName={settings?.siteName || 'Portfolio'} />

      {/* Modals */}
      <ProjectModal slug={activeProjectSlug} onClose={() => setActiveProjectSlug(null)} />
      <BlogModal slug={activeBlogSlug} onClose={() => setActiveBlogSlug(null)} />
    </div>
  );
};
