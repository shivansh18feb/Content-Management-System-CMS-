import React, { useEffect, useState } from 'react';
import {
  Plus,
  Search,
  ExternalLink,
  Edit2,
  Trash2,
  Globe,
  Archive,
  Eye,
  X,
  CheckCircle,
  FileCode
} from 'lucide-react';
import { api } from '../services/api';
import { ApiResponse, PagedResponse, ProjectSummary, ProjectDetail } from '../types';

export const Projects: React.FC = () => {
  const [projects, setProjects] = useState<ProjectSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('');
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState({
    title: '',
    slug: '',
    shortDescription: '',
    description: '',
    thumbnailUrl: '',
    githubUrl: '',
    liveUrl: '',
    documentationUrl: '',
    category: 'Full-Stack',
    featured: false,
    status: 'DRAFT' as 'DRAFT' | 'PUBLISHED' | 'ARCHIVED',
    technologies: '' as string,
    displayOrder: 0,
    startDate: '',
    endDate: '',
  });
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);

  useEffect(() => {
    fetchProjects();
  }, [search, category, status, page]);

  const fetchProjects = async () => {
    try {
      setLoading(true);
      const params: any = { page, size: 10 };
      if (search) params.search = search;
      if (category) params.category = category;
      if (status) params.status = status;

      const res = await api.get<ApiResponse<PagedResponse<ProjectSummary>>>('/api/admin/projects', { params });
      if (res.data.success) {
        setProjects(res.data.data.content);
        setTotalPages(res.data.data.totalPages || 1);
      }
    } catch (err: any) {
      console.error('Failed to load projects', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenCreate = () => {
    setEditingId(null);
    setFormData({
      title: '',
      slug: '',
      shortDescription: '',
      description: '',
      thumbnailUrl: '',
      githubUrl: '',
      liveUrl: '',
      documentationUrl: '',
      category: 'Full-Stack',
      featured: false,
      status: 'DRAFT',
      technologies: 'Java, Spring Boot, React, PostgreSQL',
      displayOrder: 0,
      startDate: '',
      endDate: '',
    });
    setFormError(null);
    setIsModalOpen(true);
  };

  const handleOpenEdit = async (id: number) => {
    try {
      setLoading(true);
      const res = await api.get<ApiResponse<ProjectDetail>>(`/api/admin/projects/${id}`);
      if (res.data.success) {
        const p = res.data.data;
        setEditingId(id);
        setFormData({
          title: p.title,
          slug: p.slug,
          shortDescription: p.shortDescription,
          description: p.description,
          thumbnailUrl: p.thumbnailUrl || '',
          githubUrl: p.githubUrl || '',
          liveUrl: p.liveUrl || '',
          documentationUrl: p.documentationUrl || '',
          category: p.category,
          featured: p.featured,
          status: p.status,
          technologies: Array.isArray(p.technologies) ? p.technologies.join(', ') : '',
          displayOrder: p.displayOrder,
          startDate: p.startDate || '',
          endDate: p.endDate || '',
        });
        setFormError(null);
        setIsModalOpen(true);
      }
    } catch (err: any) {
      alert(err.message || 'Failed to fetch project detail');
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setFormError(null);

    const payload = {
      ...formData,
      technologies: formData.technologies
        ? formData.technologies.split(',').map((t) => t.trim()).filter(Boolean)
        : [],
    };

    try {
      if (editingId) {
        await api.put(`/api/admin/projects/${editingId}`, payload);
      } else {
        await api.post('/api/admin/projects', payload);
      }
      setIsModalOpen(false);
      fetchProjects();
    } catch (err: any) {
      setFormError(err.response?.data?.message || err.message || 'Failed to save project');
    } finally {
      setSaving(false);
    }
  };

  const handlePublishToggle = async (id: number, currentStatus: string) => {
    try {
      if (currentStatus === 'PUBLISHED') {
        await api.patch(`/api/admin/projects/${id}/unpublish`);
      } else {
        await api.patch(`/api/admin/projects/${id}/publish`);
      }
      fetchProjects();
    } catch (err: any) {
      alert(err.message || 'Status toggle failed');
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to permanently delete this project?')) return;
    try {
      await api.delete(`/api/admin/projects/${id}`);
      fetchProjects();
    } catch (err: any) {
      alert(err.message || 'Delete failed');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header & Controls */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Projects Management</h1>
          <p className="text-sm text-slate-400 mt-1">Manage, showcase and publish your portfolio software case studies</p>
        </div>
        <button
          onClick={handleOpenCreate}
          className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium px-4 py-2.5 rounded-xl transition shadow-lg shadow-indigo-600/30"
        >
          <Plus className="w-4 h-4" />
          <span>New Project</span>
        </button>
      </div>

      {/* Filter Bar */}
      <div className="flex flex-col sm:flex-row gap-3 bg-slate-900 p-4 rounded-xl border border-slate-800">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search projects..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-lg pl-10 pr-4 py-2 text-sm text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500"
          />
        </div>
        <select
          value={category}
          onChange={(e) => setCategory(e.target.value)}
          className="bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-indigo-500"
        >
          <option value="">All Categories</option>
          <option value="Full-Stack">Full-Stack</option>
          <option value="Backend">Backend</option>
          <option value="DevOps">DevOps</option>
          <option value="Frontend">Frontend</option>
          <option value="Distributed Systems">Distributed Systems</option>
        </select>
        <select
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          className="bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-slate-300 focus:outline-none focus:border-indigo-500"
        >
          <option value="">All Statuses</option>
          <option value="PUBLISHED">Published</option>
          <option value="DRAFT">Draft</option>
          <option value="ARCHIVED">Archived</option>
        </select>
      </div>

      {/* Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-xl">
        {loading ? (
          <div className="p-12 text-center text-slate-500">
            <div className="w-6 h-6 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin mx-auto mb-2"></div>
            Loading projects...
          </div>
        ) : projects.length === 0 ? (
          <div className="p-12 text-center text-slate-500">
            <FileCode className="w-10 h-10 mx-auto mb-2 text-slate-600" />
            <p className="text-base font-medium text-slate-400">No projects found</p>
            <p className="text-sm mt-1">Try adjusting your filters or click 'New Project' to add one.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/60 border-b border-slate-800 text-xs font-semibold uppercase text-slate-400">
                <tr>
                  <th className="py-3.5 px-4">Project</th>
                  <th className="py-3.5 px-4">Category</th>
                  <th className="py-3.5 px-4">Tech Stack</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800">
                {projects.map((p) => (
                  <tr key={p.id} className="hover:bg-slate-800/40 transition">
                    <td className="py-4 px-4">
                      <div>
                        <span className="font-semibold text-white text-sm">{p.title}</span>
                        {p.featured && (
                          <span className="ml-2 text-[10px] bg-amber-500/10 text-amber-400 border border-amber-500/20 px-1.5 py-0.5 rounded font-medium">
                            Featured
                          </span>
                        )}
                        <p className="text-xs text-slate-400 mt-0.5 line-clamp-1">{p.shortDescription}</p>
                      </div>
                    </td>
                    <td className="py-4 px-4 text-xs font-medium text-slate-300">
                      {p.category}
                    </td>
                    <td className="py-4 px-4">
                      <div className="flex flex-wrap gap-1 max-w-xs">
                        {p.technologies?.slice(0, 3).map((t, idx) => (
                          <span key={idx} className="text-[11px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded font-mono">
                            {t}
                          </span>
                        ))}
                        {p.technologies?.length > 3 && (
                          <span className="text-[10px] text-slate-500">+{p.technologies.length - 3}</span>
                        )}
                      </div>
                    </td>
                    <td className="py-4 px-4">
                      <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                        p.status === 'PUBLISHED' ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' :
                        p.status === 'ARCHIVED' ? 'bg-slate-800 text-slate-400' :
                        'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                      }`}>
                        {p.status}
                      </span>
                    </td>
                    <td className="py-4 px-4 text-right space-x-1">
                      <button
                        onClick={() => handlePublishToggle(p.id, p.status)}
                        title={p.status === 'PUBLISHED' ? 'Unpublish (draft)' : 'Publish to live site'}
                        className={`p-1.5 rounded-lg border transition ${
                          p.status === 'PUBLISHED'
                            ? 'bg-amber-500/10 text-amber-400 border-amber-500/20 hover:bg-amber-500/20'
                            : 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20 hover:bg-emerald-500/20'
                        }`}
                      >
                        <Globe className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={() => handleOpenEdit(p.id)}
                        title="Edit project"
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 transition"
                      >
                        <Edit2 className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={() => handleDelete(p.id)}
                        title="Delete project"
                        className="p-1.5 rounded-lg bg-red-500/10 hover:bg-red-500/20 text-red-400 border border-red-500/20 transition"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {/* Pagination */}
        {totalPages > 1 && (
          <div className="flex justify-between items-center px-4 py-3 border-t border-slate-800 bg-slate-950/40 text-xs text-slate-400">
            <span>Page {page + 1} of {totalPages}</span>
            <div className="space-x-2">
              <button
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                className="px-3 py-1 bg-slate-800 rounded border border-slate-700 disabled:opacity-40"
              >
                Prev
              </button>
              <button
                disabled={page + 1 >= totalPages}
                onClick={() => setPage((p) => p + 1)}
                className="px-3 py-1 bg-slate-800 rounded border border-slate-700 disabled:opacity-40"
              >
                Next
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Create / Edit Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-2xl max-h-[90vh] flex flex-col shadow-2xl">
            <div className="flex items-center justify-between p-6 border-b border-slate-800">
              <h2 className="text-lg font-bold text-white">
                {editingId ? 'Edit Project' : 'Create New Project'}
              </h2>
              <button
                onClick={() => setIsModalOpen(false)}
                className="text-slate-400 hover:text-white p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 overflow-y-auto space-y-4 flex-1">
              {formError && (
                <div className="p-3 bg-red-500/10 border border-red-500/20 text-red-400 rounded-lg text-xs">
                  {formError}
                </div>
              )}

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div className="sm:col-span-2">
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Title *</label>
                  <input
                    type="text"
                    required
                    value={formData.title}
                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="e.g. Distributed Meta-File Storage"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Custom Slug (optional)</label>
                  <input
                    type="text"
                    value={formData.slug}
                    onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="auto-generated-if-empty"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Category</label>
                  <input
                    type="text"
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="Full-Stack, Backend, Cloud..."
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Short Description *</label>
                <input
                  type="text"
                  required
                  value={formData.shortDescription}
                  onChange={(e) => setFormData({ ...formData, shortDescription: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                  placeholder="One-line elevator pitch for homepage cards"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Full Description (Markdown supported) *</label>
                <textarea
                  required
                  rows={4}
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-3 text-sm text-white focus:outline-none focus:border-indigo-500 font-mono text-xs"
                  placeholder="Comprehensive case study, architecture details, performance benchmarks..."
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Thumbnail Image URL</label>
                  <input
                    type="url"
                    value={formData.thumbnailUrl}
                    onChange={(e) => setFormData({ ...formData, thumbnailUrl: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="https://..."
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Technologies (comma separated)</label>
                  <input
                    type="text"
                    value={formData.technologies}
                    onChange={(e) => setFormData({ ...formData, technologies: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="Java, Spring Boot, PostgreSQL, Docker"
                  />
                </div>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">GitHub Repository</label>
                  <input
                    type="url"
                    value={formData.githubUrl}
                    onChange={(e) => setFormData({ ...formData, githubUrl: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="https://github.com/..."
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Live URL</label>
                  <input
                    type="url"
                    value={formData.liveUrl}
                    onChange={(e) => setFormData({ ...formData, liveUrl: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="https://..."
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Docs URL</label>
                  <input
                    type="url"
                    value={formData.documentationUrl}
                    onChange={(e) => setFormData({ ...formData, documentationUrl: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="https://..."
                  />
                </div>
              </div>

              <div className="flex items-center space-x-6 pt-2">
                <label className="flex items-center space-x-2 text-sm text-slate-300 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={formData.featured}
                    onChange={(e) => setFormData({ ...formData, featured: e.target.checked })}
                    className="rounded bg-slate-950 border-slate-800 text-indigo-600 focus:ring-0"
                  />
                  <span>Feature on homepage showcase</span>
                </label>

                <div className="flex items-center space-x-2 text-sm text-slate-300">
                  <span>Initial Status:</span>
                  <select
                    value={formData.status}
                    onChange={(e) => setFormData({ ...formData, status: e.target.value as any })}
                    className="bg-slate-950 border border-slate-800 rounded px-2 py-1 text-xs text-white"
                  >
                    <option value="DRAFT">Draft</option>
                    <option value="PUBLISHED">Published</option>
                    <option value="ARCHIVED">Archived</option>
                  </select>
                </div>
              </div>

              <div className="flex justify-end space-x-3 pt-4 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-lg text-sm transition"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium rounded-lg text-sm transition disabled:opacity-50 flex items-center space-x-2 shadow-lg shadow-indigo-600/30"
                >
                  {saving ? <span>Saving...</span> : <span>Save Project</span>}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
