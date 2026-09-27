import React, { useEffect, useState } from 'react';
import { X, Clock, Calendar, User } from 'lucide-react';
import { publicApi } from '../services/api';
import { BlogDetail } from '../types';

interface BlogModalProps {
  slug: string | null;
  onClose: () => void;
}

export const BlogModal: React.FC<BlogModalProps> = ({ slug, onClose }) => {
  const [blog, setBlog] = useState<BlogDetail | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!slug) return;
    const fetchBlog = async () => {
      try {
        setLoading(true);
        const data = await publicApi.getBlogBySlug(slug);
        setBlog(data);
      } catch (err) {
        console.error('Failed to load blog post', err);
      } finally {
        setLoading(false);
      }
    };
    fetchBlog();
  }, [slug]);

  if (!slug) return null;

  return (
    <div className="fixed inset-0 z-50 bg-black/80 backdrop-blur-md flex items-center justify-center p-4 overflow-y-auto">
      <div className="bg-slate-900 border border-slate-800 rounded-3xl w-full max-w-3xl max-h-[90vh] flex flex-col shadow-2xl overflow-hidden relative">
        <button
          onClick={onClose}
          className="absolute top-4 right-4 z-10 p-2 rounded-full bg-slate-950/60 text-slate-400 hover:text-white border border-slate-800 backdrop-blur-sm transition"
        >
          <X className="w-5 h-5" />
        </button>

        {loading ? (
          <div className="p-20 text-center text-slate-500">
            <div className="w-8 h-8 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin mx-auto mb-3"></div>
            Loading article...
          </div>
        ) : !blog ? (
          <div className="p-12 text-center text-red-400">Article could not be loaded.</div>
        ) : (
          <div className="overflow-y-auto p-6 sm:p-10 space-y-6">
            <div>
              <div className="flex flex-wrap items-center gap-3 text-xs text-slate-400 mb-3">
                <span className="flex items-center gap-1">
                  <User className="w-3.5 h-3.5 text-indigo-400" />
                  {blog.author}
                </span>
                <span>•</span>
                <span className="flex items-center gap-1">
                  <Clock className="w-3.5 h-3.5 text-indigo-400" />
                  {blog.readingTimeMinutes} min read
                </span>
                {blog.publishedAt && (
                  <>
                    <span>•</span>
                    <span className="flex items-center gap-1">
                      <Calendar className="w-3.5 h-3.5 text-indigo-400" />
                      {new Date(blog.publishedAt).toLocaleDateString()}
                    </span>
                  </>
                )}
              </div>

              <h1 className="text-2xl sm:text-3xl font-extrabold text-white leading-tight">
                {blog.title}
              </h1>

              <div className="flex flex-wrap gap-1.5 mt-4">
                {blog.tags?.map((t) => (
                  <span
                    key={t.id}
                    className="text-xs bg-slate-950 text-indigo-300 border border-slate-800 px-2.5 py-0.5 rounded-full font-mono"
                  >
                    #{t.name}
                  </span>
                ))}
              </div>
            </div>

            <div className="border-t border-slate-800 pt-6 prose prose-invert max-w-none text-slate-300 text-sm leading-relaxed whitespace-pre-line">
              {blog.content}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
