import React, { useEffect, useState, useRef } from 'react';
import { Upload, Copy, Trash2, Check, File, Image as ImageIcon, Search } from 'lucide-react';
import { api, API_BASE_URL } from '../services/api';
import { ApiResponse, PagedResponse, MediaFile } from '../types';

export const MediaLibrary: React.FC = () => {
  const [files, setFiles] = useState<MediaFile[]>([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [copiedId, setCopiedId] = useState<number | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    fetchMedia();
  }, []);

  const fetchMedia = async () => {
    try {
      setLoading(true);
      const res = await api.get<ApiResponse<PagedResponse<MediaFile>>>('/api/admin/media');
      if (res.data.success) {
        setFiles(res.data.data.content);
      }
    } catch (err) {
      console.error('Failed to load media files', err);
    } finally {
      setLoading(false);
    }
  };

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (!selectedFile) return;

    const formData = new FormData();
    formData.append('file', selectedFile);

    setUploading(true);
    try {
      await api.post('/api/admin/media', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      fetchMedia();
    } catch (err: any) {
      alert(err.response?.data?.message || err.message || 'Upload failed');
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = '';
    }
  };

  const handleCopyUrl = (id: number, url: string) => {
    // If url is relative, construct full URL
    const fullUrl = url.startsWith('http') ? url : `${API_BASE_URL}${url}`;
    navigator.clipboard.writeText(fullUrl);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Delete this file permanently?')) return;
    try {
      await api.delete(`/api/admin/media/${id}`);
      fetchMedia();
    } catch (err: any) {
      alert(err.message || 'Delete failed');
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return bytes + ' B';
    else if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Media & Asset Library</h1>
          <p className="text-sm text-slate-400 mt-1">Upload images, resumes, and assets served directly from your storage</p>
        </div>

        <div>
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileUpload}
            className="hidden"
            accept="image/*,application/pdf"
          />
          <button
            onClick={() => fileInputRef.current?.click()}
            disabled={uploading}
            className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium px-4 py-2.5 rounded-xl transition shadow-lg shadow-indigo-600/30 disabled:opacity-50"
          >
            <Upload className="w-4 h-4" />
            <span>{uploading ? 'Uploading...' : 'Upload Media'}</span>
          </button>
        </div>
      </div>

      <div className="bg-slate-900 border border-slate-800 rounded-2xl p-6 shadow-xl">
        {loading ? (
          <div className="p-12 text-center text-slate-500">Loading media library...</div>
        ) : files.length === 0 ? (
          <div className="p-12 text-center text-slate-500">
            <ImageIcon className="w-10 h-10 mx-auto mb-2 text-slate-600" />
            <p className="text-base font-medium text-slate-400">No media uploaded yet</p>
            <p className="text-sm mt-1">Click 'Upload Media' to add project screenshots, blog covers, or resume PDFs.</p>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
            {files.map((file) => {
              const fullUrl = file.fileUrl.startsWith('http')
  ? file.fileUrl
  : `${API_BASE_URL}${file.fileUrl}`;
              const isImage = file.contentType.startsWith('image/');
              return (
                <div
                  key={file.id}
                  className="bg-slate-950 border border-slate-800 rounded-xl overflow-hidden group hover:border-slate-700 transition flex flex-col justify-between"
                >
                  <div className="h-32 bg-slate-900 flex items-center justify-center overflow-hidden relative">
                    {isImage ? (
                      <img
                        src={fullUrl}
                        alt={file.originalFilename}
                        className="w-full h-full object-cover group-hover:scale-105 transition duration-300"
                        onError={(e) => {
                          (e.target as any).src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100"><rect width="100%" height="100%" fill="%231e293b"/></svg>';
                        }}
                      />
                    ) : (
                      <File className="w-10 h-10 text-slate-500" />
                    )}
                  </div>

                  <div className="p-3">
                    <p className="text-xs font-semibold text-slate-200 truncate" title={file.originalFilename}>
                      {file.originalFilename}
                    </p>
                    <p className="text-[10px] text-slate-500 mt-0.5">{formatFileSize(file.fileSize)}</p>

                    <div className="flex justify-between items-center mt-3 pt-2 border-t border-slate-800">
                      <button
                        onClick={() => handleCopyUrl(file.id, file.fileUrl)}
                        title="Copy direct link"
                        className="text-slate-400 hover:text-white p-1 rounded transition flex items-center gap-1 text-[11px]"
                      >
                        {copiedId === file.id ? (
                          <Check className="w-3.5 h-3.5 text-emerald-400" />
                        ) : (
                          <Copy className="w-3.5 h-3.5" />
                        )}
                        <span>{copiedId === file.id ? 'Copied' : 'Copy'}</span>
                      </button>

                      <button
                        onClick={() => handleDelete(file.id)}
                        title="Delete asset"
                        className="text-slate-500 hover:text-red-400 p-1 rounded transition"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};
