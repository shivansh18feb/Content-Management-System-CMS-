import { Code2 } from 'lucide-react';
import { SocialLink } from '../types';

interface FooterProps {
  socialLinks?: SocialLink[];
  siteName?: string;
}

export const Footer: React.FC<FooterProps> = ({ socialLinks = [], siteName = 'Portfolio' }) => {
  return (
    <footer className="border-t border-slate-900 bg-slate-950 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-6">
        <div className="flex items-center space-x-3">
          <div className="w-7 h-7 rounded-lg bg-indigo-600 flex items-center justify-center font-bold text-white text-xs">
            P
          </div>
          <span className="font-semibold text-slate-300 text-sm">{siteName}</span>
          <span className="text-xs text-slate-600">•</span>
          <p className="text-xs text-slate-500">
            © {new Date().getFullYear()} All rights reserved.
          </p>
        </div>

        <div className="flex items-center space-x-2 text-xs text-slate-500">
          <Code2 className="w-3.5 h-3.5 text-indigo-400" />
          <span>Built from scratch with Spring Boot 3 & React</span>
        </div>

        <div className="flex items-center space-x-4">
          {socialLinks.map((link, idx) => (
            <a
              key={idx}
              href={link.url}
              target="_blank"
              rel="noopener noreferrer"
              className="text-slate-400 hover:text-indigo-400 transition text-xs font-medium"
            >
              {link.platform}
            </a>
          ))}
        </div>
      </div>
    </footer>
  );
};
