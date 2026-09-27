import React, { useEffect, useState } from 'react';
import {
  FolderGit2,
  FileText,
  Wrench,
  Mail,
  Sparkles,
  TrendingUp,
  Clock,
  CheckCircle2,
  AlertTriangle
} from 'lucide-react';
import { api } from '../services/api';
import { ApiResponse, DashboardStats } from '../types';

export const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      setLoading(true);
      const res = await api.get<ApiResponse<DashboardStats>>('/api/admin/dashboard/stats');
      if (res.data.success) {
        setStats(res.data.data);
      }
    } catch (err: any) {
      setError(err?.message || 'Failed to load dashboard metrics');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin"></div>
      </div>
    );
  }

  if (error || !stats) {
    return (
      <div className="p-6 bg-red-500/10 border border-red-500/20 rounded-2xl text-red-400">
        <p className="font-semibold">Error loading statistics</p>
        <p className="text-sm mt-1">{error}</p>
        <button
          onClick={fetchStats}
          className="mt-4 px-4 py-2 bg-red-600/20 hover:bg-red-600/30 text-white rounded-lg text-sm transition"
        >
          Retry
        </button>
      </div>
    );
  }

  const statCards = [
    {
      title: 'Projects Showcase',
      value: stats.totalProjects,
      subtext: `${stats.publishedProjects} Published • ${stats.draftProjects} Draft`,
      icon: FolderGit2,
      color: 'from-blue-600 to-indigo-600',
    },
    {
      title: 'Blog Articles',
      value: stats.totalBlogs,
      subtext: `${stats.publishedBlogs} Published • ${stats.draftBlogs} Draft`,
      icon: FileText,
      color: 'from-purple-600 to-pink-600',
    },
    {
      title: 'Technical Skills',
      value: stats.totalSkills,
      subtext: `${stats.totalSkillCategories} Categories`,
      icon: Wrench,
      color: 'from-amber-500 to-orange-600',
    },
    {
      title: 'Client Inquiries',
      value: stats.totalMessages,
      subtext: `${stats.unreadMessages} Unread messages`,
      icon: Mail,
      color: 'from-emerald-500 to-teal-600',
    },
  ];

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 bg-gradient-to-r from-slate-900 to-slate-800 p-6 rounded-2xl border border-slate-800">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">System Overview</h1>
          <p className="text-sm text-slate-400 mt-1">Live metrics from your Spring Boot REST API & PostgreSQL database</p>
        </div>
        <div className="flex items-center space-x-2 text-xs font-mono bg-slate-950 px-3 py-1.5 rounded-lg border border-slate-800 text-slate-400">
          <Clock className="w-3.5 h-3.5 text-indigo-400" />
          <span>Last sync: Just now</span>
        </div>
      </div>

      {/* Metrics Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
        {statCards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <div
              key={idx}
              className="bg-slate-900 border border-slate-800 rounded-2xl p-5 hover:border-slate-700 transition relative overflow-hidden"
            >
              <div className="flex justify-between items-start">
                <div>
                  <p className="text-xs font-medium text-slate-400 uppercase tracking-wider">{card.title}</p>
                  <h3 className="text-3xl font-extrabold text-white mt-2">{card.value}</h3>
                  <p className="text-xs text-slate-400 mt-2 flex items-center gap-1">{card.subtext}</p>
                </div>
                <div className={`p-3 rounded-xl bg-gradient-to-br ${card.color} text-white shadow-lg`}>
                  <Icon className="w-5 h-5" />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Secondary Metrics & Quick Counters */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="bg-slate-900/60 border border-slate-800/80 p-4 rounded-xl">
          <span className="text-xs text-slate-400">Work Experience</span>
          <p className="text-xl font-bold text-slate-200 mt-1">{stats.totalExperiences} Roles</p>
        </div>
        <div className="bg-slate-900/60 border border-slate-800/80 p-4 rounded-xl">
          <span className="text-xs text-slate-400">Services Offered</span>
          <p className="text-xl font-bold text-slate-200 mt-1">{stats.totalServices} Services</p>
        </div>
        <div className="bg-slate-900/60 border border-slate-800/80 p-4 rounded-xl">
          <span className="text-xs text-slate-400">Testimonials</span>
          <p className="text-xl font-bold text-slate-200 mt-1">{stats.publishedTestimonials} Published</p>
        </div>
        <div className="bg-slate-900/60 border border-slate-800/80 p-4 rounded-xl">
          <span className="text-xs text-slate-400">Media Files</span>
          <p className="text-xl font-bold text-slate-200 mt-1">{stats.totalMediaFiles} Uploads</p>
        </div>
      </div>

      {/* Recent Activity Feed */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center space-x-2">
            <TrendingUp className="w-5 h-5 text-indigo-400" />
            <h2 className="text-base font-semibold text-white">Recent Audit Trail</h2>
          </div>
          <span className="text-xs text-slate-500">Latest 10 security events</span>
        </div>

        {stats.recentActivity.length === 0 ? (
          <p className="text-sm text-slate-500 py-4 text-center">No recent security events logged.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="border-b border-slate-800 text-xs font-semibold uppercase text-slate-400 tracking-wider">
                <tr>
                  <th className="pb-3 px-3">Action</th>
                  <th className="pb-3 px-3">Entity</th>
                  <th className="pb-3 px-3">User</th>
                  <th className="pb-3 px-3">Timestamp</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {stats.recentActivity.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-800/30 transition">
                    <td className="py-3 px-3">
                      <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-semibold ${
                        log.action === 'CREATE' ? 'bg-emerald-500/10 text-emerald-400' :
                        log.action === 'UPDATE' ? 'bg-blue-500/10 text-blue-400' :
                        log.action === 'DELETE' ? 'bg-red-500/10 text-red-400' :
                        log.action === 'LOGIN' ? 'bg-indigo-500/10 text-indigo-400' :
                        'bg-slate-800 text-slate-300'
                      }`}>
                        {log.action}
                      </span>
                    </td>
                    <td className="py-3 px-3 text-slate-300 font-mono text-xs">
                      {log.entityType} {log.entityId ? `#${log.entityId}` : ''}
                    </td>
                    <td className="py-3 px-3 text-slate-400 text-xs">
                      {log.userEmail || 'System'}
                    </td>
                    <td className="py-3 px-3 text-slate-500 text-xs font-mono">
                      {new Date(log.createdAt).toLocaleString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
