import React, { useEffect, useState } from 'react';
import {
  Plus,
  Search,
  Edit2,
  Trash2,
  Globe,
  FileText,
  X,
  Clock,
  Tag as TagIcon
} from 'lucide-react';
import { api } from '../services/api';
import { ApiResponse, PagedResponse, BlogSummary, BlogDetail } from '../types';

export const Blogs: React.FC = () => {
  const [blogs, setBlogs] = useState<BlogSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  // Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState({
    title: '',
    slug: '',
    excerpt: '',
    content: '',
    coverImageUrl: '',
    author: 'System Administrator',
    status: 'DRAFT' as 'DRAFT' | 'PUBLISHED' | 'ARCHIVED',
    readingTimeMinutes: 5,
    tagNames: '' as string,
    seoTitle: '',
    seoDescription: '',
    seoKeywords: '',
  });
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);

  useEffect(() => {
    fetchBlogs();
  }, [search, status, page]);

  const fetchBlogs = async () => {
    try {
      setLoading(true);
      const params: any = { page, size: 10 };
      if (search) params.search = search;
      if (status) params.status = status;

      const res = await api.get<ApiResponse<PagedResponse<BlogSummary>>>('/api/admin/blogs', { params });
      if (res.data.success) {
        setBlogs(res.data.data.content);
        setTotalPages(res.data.data.totalPages || 1);
      }
    } catch (err: any) {
      console.error('Failed to load blogs', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenCreate = () => {
    setEditingId(null);
    setFormData({
      title: '',
      slug: '',
      excerpt: '',
      content: '## Overview\n\nWrite your technical article here in Markdown...',
      coverImageUrl: '',
      author: 'System Administrator',
      status: 'DRAFT',
      readingTimeMinutes: 5,
      tagNames: 'Java, Architecture, Best Practices',
      seoTitle: '',
      seoDescription: '',
      seoKeywords: '',
    });
    setFormError(null);
    setIsModalOpen(true);
  };

  const handleOpenEdit = async (id: number) => {
    try {
      setLoading(true);
      const res = await api.get<ApiResponse<BlogDetail>>(`/api/admin/blogs/${id}`);
      if (res.data.success) {
        const b = res.data.data;
        setEditingId(id);
        setFormData({
          title: b.title,
          slug: b.slug,
          excerpt: b.excerpt,
          content: b.content,
          coverImageUrl: b.coverImageUrl || '',
          author: b.author,
          status: b.status,
          readingTimeMinutes: b.readingTimeMinutes,
          tagNames: b.tags?.map((t) => t.name).join(', ') || '',
          seoTitle: b.seoTitle || '',
          seoDescription: b.seoDescription || '',
          seoKeywords: b.seoKeywords || '',
        });
        setFormError(null);
        setIsModalOpen(true);
      }
    } catch (err: any) {
      alert(err.message || 'Failed to fetch article detail');
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
      tagNames: formData.tagNames
        ? formData.tagNames.split(',').map((t) => t.trim()).filter(Boolean)
        : [],
    };

    try {
      if (editingId) {
        await api.put(`/api/admin/blogs/${editingId}`, payload);
      } else {
        await api.post('/api/admin/blogs', payload);
      }
      setIsModalOpen(false);
      fetchBlogs();
    } catch (err: any) {
      setFormError(err.response?.data?.message || err.message || 'Failed to save blog post');
    } finally {
      setSaving(false);
    }
  };

  const handlePublishToggle = async (id: number, currentStatus: string) => {
    try {
      if (currentStatus === 'PUBLISHED') {
        await api.patch(`/api/admin/blogs/${id}/unpublish`);
      } else {
        await api.patch(`/api/admin/blogs/${id}/publish`);
      }
      fetchBlogs();
    } catch (err: any) {
      alert(err.message || 'Publish toggle failed');
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to permanently delete this article?')) return;
    try {
      await api.delete(`/api/admin/blogs/${id}`);
      fetchBlogs();
    } catch (err: any) {
      alert(err.message || 'Delete failed');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Technical Articles & Blog</h1>
          <p className="text-sm text-slate-400 mt-1">Publish engineering deep dives, tutorials, and architecture posts</p>
        </div>
        <button
          onClick={handleOpenCreate}
          className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium px-4 py-2.5 rounded-xl transition shadow-lg shadow-indigo-600/30"
        >
          <Plus className="w-4 h-4" />
          <span>Write Article</span>
        </button>
      </div>

      {/* Filter Bar */}
      <div className="flex flex-col sm:flex-row gap-3 bg-slate-900 p-4 rounded-xl border border-slate-800">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search articles by title or excerpt..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="w-full bg-slate-950 border border-slate-800 rounded-lg pl-10 pr-4 py-2 text-sm text-white placeholder-slate-500 focus:outline-none focus:border-indigo-500"
          />
        </div>
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
            Loading articles...
          </div>
        ) : blogs.length === 0 ? (
          <div className="p-12 text-center text-slate-500">
            <FileText className="w-10 h-10 mx-auto mb-2 text-slate-600" />
            <p className="text-base font-medium text-slate-400">No articles found</p>
            <p className="text-sm mt-1">Click 'Write Article' to compose your first blog post.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/60 border-b border-slate-800 text-xs font-semibold uppercase text-slate-400">
                <tr>
                  <th className="py-3.5 px-4">Article Title</th>
                  <th className="py-3.5 px-4">Tags</th>
                  <th className="py-3.5 px-4">Read Time</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800">
                {blogs.map((b) => (
                  <tr key={b.id} className="hover:bg-slate-800/40 transition">
                    <td className="py-4 px-4">
                      <div>
                        <span className="font-semibold text-white text-sm">{b.title}</span>
                        <p className="text-xs text-slate-400 mt-0.5 line-clamp-1">{b.excerpt}</p>
                      </div>
                    </td>
                    <td className="py-4 px-4">
                      <div className="flex flex-wrap gap-1 max-w-xs">
                        {b.tags?.map((t) => (
                          <span key={t.id} className="text-[10px] bg-slate-800 text-slate-300 px-2 py-0.5 rounded font-mono">
                            #{t.name}
                          </span>
                        ))}
                      </div>
                    </td>
                    <td className="py-4 px-4 text-xs text-slate-400">
                      <span className="flex items-center gap-1">
                        <Clock className="w-3.5 h-3.5 text-slate-500" />
                        {b.readingTimeMinutes} min
                      </span>
                    </td>
                    <td className="py-4 px-4">
                      <span className={`inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium ${
                        b.status === 'PUBLISHED' ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20' :
                        b.status === 'ARCHIVED' ? 'bg-slate-800 text-slate-400' :
                        'bg-amber-500/10 text-amber-400 border border-amber-500/20'
                      }`}>
                        {b.status}
                      </span>
                    </td>
                    <td className="py-4 px-4 text-right space-x-1">
                      <button
                        onClick={() => handlePublishToggle(b.id, b.status)}
                        title={b.status === 'PUBLISHED' ? 'Unpublish (draft)' : 'Publish to live site'}
                        className={`p-1.5 rounded-lg border transition ${
                          b.status === 'PUBLISHED'
                            ? 'bg-amber-500/10 text-amber-400 border-amber-500/20 hover:bg-amber-500/20'
                            : 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20 hover:bg-emerald-500/20'
                        }`}
                      >
                        <Globe className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={() => handleOpenEdit(b.id)}
                        title="Edit article"
                        className="p-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-300 border border-slate-700 transition"
                      >
                        <Edit2 className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={() => handleDelete(b.id)}
                        title="Delete article"
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

      {/* Editor Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 overflow-y-auto">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-3xl max-h-[92vh] flex flex-col shadow-2xl">
            <div className="flex items-center justify-between p-6 border-b border-slate-800">
              <h2 className="text-lg font-bold text-white">
                {editingId ? 'Edit Article' : 'Compose New Article'}
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

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div className="sm:col-span-2">
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Title *</label>
                  <input
                    type="text"
                    required
                    value={formData.title}
                    onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="e.g. Modern Virtual Threads with Java 21"
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Custom Slug (optional)</label>
                  <input
                    type="text"
                    value={formData.slug}
                    onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="auto-generated"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Excerpt / Summary *</label>
                <textarea
                  required
                  rows={2}
                  value={formData.excerpt}
                  onChange={(e) => setFormData({ ...formData, excerpt: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-sm text-white focus:outline-none focus:border-indigo-500"
                  placeholder="Engaging summary for blog listing cards and search engines"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">
                  Article Markdown Content *
                </label>
                <textarea
                  required
                  rows={10}
                  value={formData.content}
                  onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg p-3 text-sm text-white focus:outline-none focus:border-indigo-500 font-mono text-xs leading-relaxed"
                  placeholder="# Article Headline\n\nContent..."
                />
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Tags (comma separated)</label>
                  <input
                    type="text"
                    value={formData.tagNames}
                    onChange={(e) => setFormData({ ...formData, tagNames: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="Java, Spring, Microservices"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Cover Image URL</label>
                  <input
                    type="url"
                    value={formData.coverImageUrl}
                    onChange={(e) => setFormData({ ...formData, coverImageUrl: e.target.value })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                    placeholder="https://..."
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Read Time (minutes)</label>
                  <input
                    type="number"
                    min={1}
                    value={formData.readingTimeMinutes}
                    onChange={(e) => setFormData({ ...formData, readingTimeMinutes: parseInt(e.target.value) || 3 })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                  />
                </div>
              </div>

              <div className="flex items-center space-x-4 pt-2">
                <span className="text-xs text-slate-300">Status:</span>
                <select
                  value={formData.status}
                  onChange={(e) => setFormData({ ...formData, status: e.target.value as any })}
                  className="bg-slate-950 border border-slate-800 rounded px-2.5 py-1 text-xs text-white"
                >
                  <option value="DRAFT">Draft</option>
                  <option value="PUBLISHED">Published</option>
                  <option value="ARCHIVED">Archived</option>
                </select>
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
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium rounded-lg text-sm transition disabled:opacity-50 shadow-lg shadow-indigo-600/30"
                >
                  {saving ? <span>Saving...</span> : <span>Save Article</span>}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
