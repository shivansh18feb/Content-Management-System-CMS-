import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  FolderGit2,
  FileText,
  Wrench,
  Briefcase,
  GraduationCap,
  Sparkles,
  MessageSquareQuote,
  Image as ImageIcon,
  Mail,
  Share2,
  Settings,
  ShieldAlert,
  UserCheck
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

interface NavItem {
  name: string;
  path: string;
  icon: React.ElementType;
  adminOnly?: boolean;
}

const navItems: NavItem[] = [
  { name: 'Dashboard', path: '/', icon: LayoutDashboard },
  { name: 'About Profile', path: '/about', icon: UserCheck },
  { name: 'Projects', path: '/projects', icon: FolderGit2 },
  { name: 'Blog Articles', path: '/blogs', icon: FileText },
  { name: 'Skills & Tech', path: '/skills', icon: Wrench },
  { name: 'Experience', path: '/experience', icon: Briefcase },
  { name: 'Education', path: '/education', icon: GraduationCap },
  { name: 'Services', path: '/services', icon: Sparkles },
  { name: 'Testimonials', path: '/testimonials', icon: MessageSquareQuote },
  { name: 'Media Library', path: '/media', icon: ImageIcon },
  { name: 'Messages', path: '/messages', icon: Mail },
  { name: 'Social Links', path: '/social', icon: Share2 },
  { name: 'Site Settings', path: '/settings', icon: Settings },
  { name: 'Audit Logs', path: '/audit-logs', icon: ShieldAlert, adminOnly: true },
];

export const Sidebar: React.FC = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';

  return (
    <aside className="w-64 bg-slate-900 border-r border-slate-800 flex flex-col h-screen sticky top-0 select-none">
      {/* Brand Header */}
      <div className="h-16 flex items-center px-6 border-b border-slate-800 space-x-3">
        <div className="w-9 h-9 rounded-lg bg-indigo-600 flex items-center justify-center font-bold text-white shadow-lg shadow-indigo-500/30">
          CMS
        </div>
        <div>
          <h1 className="font-semibold text-white text-sm tracking-wide">Developer CMS</h1>
          <p className="text-xs text-indigo-400 font-mono">v1.0.0 • Production</p>
        </div>
      </div>

      {/* Navigation Links */}
      <div className="flex-1 overflow-y-auto py-4 px-3 space-y-1">
        {navItems.map((item) => {
          if (item.adminOnly && !isAdmin) return null;
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.path === '/'}
              className={({ isActive }) =>
                `flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                  isActive
                    ? 'bg-indigo-600/20 text-indigo-400 border border-indigo-500/30'
                    : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'
                }`
              }
            >
              <Icon className="w-4 h-4 shrink-0" />
              <span>{item.name}</span>
            </NavLink>
          );
        })}
      </div>

      {/* User Footer Profile */}
      <div className="p-4 border-t border-slate-800 bg-slate-900/60 flex items-center justify-between">
        <div className="flex items-center space-x-3 overflow-hidden">
          <div className="w-8 h-8 rounded-full bg-slate-700 flex items-center justify-center font-semibold text-slate-300 text-xs shrink-0">
            {user?.fullName?.charAt(0) || 'A'}
          </div>
          <div className="overflow-hidden">
            <p className="text-sm font-medium text-slate-200 truncate">{user?.fullName || 'User'}</p>
            <p className="text-xs text-slate-500 truncate">{user?.email}</p>
          </div>
        </div>
        <span className={`text-[10px] uppercase font-bold px-2 py-0.5 rounded ${
          isAdmin ? 'bg-indigo-500/20 text-indigo-300' : 'bg-slate-800 text-slate-400'
        }`}>
          {user?.role}
        </span>
      </div>
    </aside>
  );
};
