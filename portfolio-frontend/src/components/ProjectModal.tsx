import React, { useEffect, useState } from 'react';
import { X, ExternalLink, Code2, BookOpen } from 'lucide-react';
import { publicApi } from '../services/api';
import { ProjectDetail } from '../types';

interface ProjectModalProps {
  slug: string | null;
  onClose: () => void;
}

export const ProjectModal: React.FC<ProjectModalProps> = ({ slug, onClose }) => {
  const [project, setProject] = useState<ProjectDetail | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!slug) return;
    const fetchDetail = async () => {
      try {
        setLoading(true);
        const data = await publicApi.getProjectBySlug(slug);
        setProject(data);
      } catch (err) {
        console.error('Failed to load project detail', err);
      } finally {
        setLoading(false);
      }
    };
    fetchDetail();
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
            Loading case study...
          </div>
        ) : !project ? (
          <div className="p-12 text-center text-red-400">Project details could not be loaded.</div>
        ) : (
          <div className="overflow-y-auto">
            {/* Header image / Banner */}
            {project.thumbnailUrl && (
              <div className="h-64 sm:h-72 w-full overflow-hidden bg-slate-950 relative">
                <img
                  src={project.thumbnailUrl}
                  alt={project.title}
                  className="w-full h-full object-cover"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-slate-900 via-transparent to-transparent"></div>
              </div>
            )}

            <div className="p-6 sm:p-8 space-y-6">
              <div>
                <span className="text-xs uppercase tracking-wider font-semibold text-indigo-400 bg-indigo-500/10 px-2.5 py-1 rounded-full border border-indigo-500/20">
                  {project.category}
                </span>
                <h2 className="text-2xl sm:text-3xl font-extrabold text-white mt-3">
                  {project.title}
                </h2>
                <p className="text-base text-slate-400 mt-2 leading-relaxed">
                  {project.shortDescription}
                </p>
              </div>

              {/* Action Links */}
              <div className="flex flex-wrap gap-3 pt-2">
                {project.liveUrl && (
                  <a
                    href={project.liveUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium px-4 py-2 rounded-xl text-sm transition shadow-lg shadow-indigo-600/30"
                  >
                    <span>Live Demo</span>
                    <ExternalLink className="w-4 h-4" />
                  </a>
                )}
                {project.githubUrl && (
                  <a
                    href={project.githubUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center space-x-2 bg-slate-800 hover:bg-slate-700 text-slate-200 font-medium px-4 py-2 rounded-xl text-sm border border-slate-700 transition"
                  >
                    <Code2 className="w-4 h-4" />
                    <span>View Repository</span>
                  </a>
                )}
                {project.documentationUrl && (
                  <a
                    href={project.documentationUrl}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="flex items-center space-x-2 bg-slate-800 hover:bg-slate-700 text-slate-200 font-medium px-4 py-2 rounded-xl text-sm border border-slate-700 transition"
                  >
                    <BookOpen className="w-4 h-4" />
                    <span>Architecture Docs</span>
                  </a>
                )}
              </div>

              {/* Tech Stack */}
              <div>
                <h4 className="text-xs font-semibold text-slate-400 uppercase tracking-wider mb-2.5">
                  Technologies & Architecture Stack
                </h4>
                <div className="flex flex-wrap gap-1.5">
                  {project.technologies?.map((tech, idx) => (
                    <span
                      key={idx}
                      className="text-xs bg-slate-950 text-indigo-300 border border-slate-800 px-3 py-1 rounded-lg font-mono"
                    >
                      {tech}
                    </span>
                  ))}
                </div>
              </div>

              {/* Case Study Full Content */}
              <div className="border-t border-slate-800 pt-6">
                <h4 className="text-sm font-bold text-white uppercase tracking-wider mb-3">
                  Technical Deep Dive & Architecture
                </h4>
                <div className="text-sm text-slate-300 leading-relaxed whitespace-pre-line space-y-4">
                  {project.description}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
