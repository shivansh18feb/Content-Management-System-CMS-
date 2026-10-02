import React, { useEffect, useRef, useState } from 'react';
import { UserCheck, Save, Check, Upload, FileText } from 'lucide-react';
import { api, API_BASE_URL } from '../services/api';
import { ApiResponse, About } from '../types';

export const AboutPage: React.FC = () => {
  const [form, setForm] = useState<Partial<About>>({
    headline: '',
    shortBio: '',
    longBio: '',
    profileImageUrl: '',
    location: '',
    email: '',
    phone: '',
    resumeUrl: '',
    availability: 'AVAILABLE',
    yearsOfExperience: 6,
    githubUrl: '',
    linkedinUrl: '',
    twitterUrl: '',
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState(false);
  const [uploadingResume, setUploadingResume] = useState(false);
  const resumeInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    fetchAbout();
  }, []);

  const fetchAbout = async () => {
    try {
      setLoading(true);
      const res = await api.get<ApiResponse<About>>('/api/public/about');
      if (res.data.success && res.data.data) {
        setForm(res.data.data);
      }
    } catch (err) {
      console.error('Failed to load about profile', err);
    } finally {
      setLoading(false);
    }
  };

  const handleResumeUpload = async (
  e: React.ChangeEvent<HTMLInputElement>
) => {
  const file = e.target.files?.[0];

  if (!file) return;

  if (file.type !== 'application/pdf') {
    alert('Please upload a PDF resume.');
    return;
  }

  const formData = new FormData();
  formData.append('file', file);

  setUploadingResume(true);

  try {
    const res = await api.post<ApiResponse<any>>(
      '/api/admin/media',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );

    if (res.data.success && res.data.data) {
      const media = res.data.data;

      const resumeUrl = media.fileUrl.startsWith('http')
  ? media.fileUrl
  : `${API_BASE_URL.replace(/\/+$/, '')}/${media.fileUrl.replace(/^\/+/, '')}`;
      setForm((prev) => ({
        ...prev,
        resumeUrl,
      }));

      alert(
        'Resume uploaded successfully. Click Save Profile Changes.'
      );
    }
  } catch (err: any) {
    alert(
      err.response?.data?.message ||
        err.message ||
        'Resume upload failed'
    );
  } finally {
    setUploadingResume(false);

    if (resumeInputRef.current) {
      resumeInputRef.current.value = '';
    }
  }
};

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setSuccess(false);
    try {
      const res = await api.put<ApiResponse<About>>('/api/admin/about', form);
      if (res.data.success) {
        setForm(res.data.data);
        setSuccess(true);
        setTimeout(() => setSuccess(false), 3000);
      }
    } catch (err: any) {
      alert(err.response?.data?.message || err.message || 'Update failed');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <div className="p-12 text-center text-slate-500">Loading profile data...</div>;
  }

  return (
    <div className="space-y-6 max-w-4xl">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Personal & Professional Profile</h1>
          <p className="text-sm text-slate-400 mt-1">Configure your hero headline, biography, resume links, and contact channels</p>
        </div>
        {success && (
          <span className="flex items-center gap-1.5 text-xs text-emerald-400 bg-emerald-500/10 border border-emerald-500/20 px-3 py-1.5 rounded-lg font-medium">
            <Check className="w-4 h-4" />
            Saved successfully!
          </span>
        )}
      </div>

      <form onSubmit={handleSubmit} className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl space-y-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div className="sm:col-span-2">
            <label className="block text-xs font-semibold text-slate-300 mb-1">Headline *</label>
            <input
              type="text"
              required
              value={form.headline || ''}
              onChange={(e) => setForm({ ...form, headline: e.target.value })}
              placeholder="e.g. Senior Full-Stack Engineer & Distributed Systems Architect"
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Location</label>
            <input
              type="text"
              value={form.location || ''}
              onChange={(e) => setForm({ ...form, location: e.target.value })}
              placeholder="e.g. San Francisco, CA / Remote"
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Availability Status</label>
            <select
              value={form.availability || 'AVAILABLE'}
              onChange={(e) => setForm({ ...form, availability: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
            >
              <option value="AVAILABLE">Available for hire / contracts</option>
              <option value="CONSIDERING">Open to compelling opportunities</option>
              <option value="UNAVAILABLE">Unavailable</option>
            </select>
          </div>
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1">Short Elevator Bio *</label>
          <textarea
            required
            rows={2}
            value={form.shortBio || ''}
            onChange={(e) => setForm({ ...form, shortBio: e.target.value })}
            placeholder="Crisp 2-sentence introduction for the hero section..."
            className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-sm text-white focus:outline-none focus:border-indigo-500"
          />
        </div>

        <div>
          <label className="block text-xs font-semibold text-slate-300 mb-1">Full Biography *</label>
          <textarea
            required
            rows={5}
            value={form.longBio || ''}
            onChange={(e) => setForm({ ...form, longBio: e.target.value })}
            placeholder="Detailed narrative covering your journey, engineering philosophy, and high-impact projects..."
            className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-sm text-white focus:outline-none focus:border-indigo-500"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Contact Email</label>
            <input
              type="email"
              value={form.email || ''}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Years Experience</label>
            <input
              type="number"
              min={0}
              value={form.yearsOfExperience || 0}
              onChange={(e) => setForm({ ...form, yearsOfExperience: parseInt(e.target.value) || 0 })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
            />
          </div>
          <div>
  <label className="block text-xs font-semibold text-slate-300 mb-1">
    Resume / CV
  </label>

  <input
    ref={resumeInputRef}
    type="file"
    accept="application/pdf"
    onChange={handleResumeUpload}
    className="hidden"
  />

  <div className="flex items-center gap-2">
    <button
      type="button"
      onClick={() => resumeInputRef.current?.click()}
      disabled={uploadingResume}
      className="flex items-center gap-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium px-4 py-2 rounded-lg transition disabled:opacity-50"
    >
      <Upload className="w-4 h-4" />
      {uploadingResume ? 'Uploading...' : 'Upload Resume'}
    </button>

    {form.resumeUrl && (
      <a
        href={form.resumeUrl}
        target="_blank"
        rel="noopener noreferrer"
        className="flex items-center gap-2 text-sm text-indigo-400 hover:text-indigo-300"
      >
        <FileText className="w-4 h-4" />
        View Resume
      </a>
    )}
  </div>

  {form.resumeUrl && (
    <p className="text-[11px] text-slate-500 mt-2 truncate">
      {form.resumeUrl}
    </p>
  )}
</div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">GitHub Profile URL</label>
            <input
              type="url"
              value={form.githubUrl || ''}
              onChange={(e) => setForm({ ...form, githubUrl: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">LinkedIn URL</label>
            <input
              type="url"
              value={form.linkedinUrl || ''}
              onChange={(e) => setForm({ ...form, linkedinUrl: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
            />
          </div>
          <div>
            <label className="block text-xs font-semibold text-slate-300 mb-1">Twitter / X URL</label>
            <input
              type="url"
              value={form.twitterUrl || ''}
              onChange={(e) => setForm({ ...form, twitterUrl: e.target.value })}
              className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
            />
          </div>
        </div>

        <div className="flex justify-end pt-4 border-t border-slate-800">
          <button
            type="submit"
            disabled={saving}
            className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium px-6 py-2.5 rounded-xl transition shadow-lg shadow-indigo-600/30 disabled:opacity-50"
          >
            <Save className="w-4 h-4" />
            <span>{saving ? 'Saving...' : 'Save Profile Changes'}</span>
          </button>
        </div>
      </form>
    </div>
  );
};
