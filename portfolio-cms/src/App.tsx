import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ProtectedRoute } from './components/ProtectedRoute';
import { Layout } from './components/Layout';

import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { AboutPage } from './pages/About';
import { Projects } from './pages/Projects';
import { Blogs } from './pages/Blogs';
import { Skills } from './pages/Skills';
import { Experiences } from './pages/Experience';
import { EducationPage } from './pages/Education';
import { ServicesPage } from './pages/Services';
import { TestimonialsPage } from './pages/Testimonials';
import { MediaLibrary } from './pages/MediaLibrary';
import { MessagesPage } from './pages/Messages';
import { SocialLinksPage } from './pages/SocialLinks';
import { SettingsPage } from './pages/Settings';
import { AuditLogsPage } from './pages/AuditLogs';

export const App: React.FC = () => {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route path="/" element={<Dashboard />} />
              <Route path="/about" element={<AboutPage />} />
              <Route path="/projects" element={<Projects />} />
              <Route path="/blogs" element={<Blogs />} />
              <Route path="/skills" element={<Skills />} />
              <Route path="/experience" element={<Experiences />} />
              <Route path="/education" element={<EducationPage />} />
              <Route path="/services" element={<ServicesPage />} />
              <Route path="/testimonials" element={<TestimonialsPage />} />
              <Route path="/media" element={<MediaLibrary />} />
              <Route path="/messages" element={<MessagesPage />} />
              <Route path="/social" element={<SocialLinksPage />} />
              <Route path="/settings" element={<SettingsPage />} />
              <Route path="/audit-logs" element={<AuditLogsPage />} />
            </Route>
          </Route>

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
};

export default App;
