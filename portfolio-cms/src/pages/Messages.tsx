import React, { useEffect, useState } from 'react';
import { Mail, MailOpen, Trash2, Archive, Search, Eye, X, Send } from 'lucide-react';
import { api } from '../services/api';
import { ApiResponse, PagedResponse, ContactMessage } from '../types';

export const MessagesPage: React.FC = () => {
  const [messages, setMessages] = useState<ContactMessage[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [status, setStatus] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const [selectedMessage, setSelectedMessage] = useState<ContactMessage | null>(null);

  useEffect(() => {
    fetchMessages();
  }, [search, status, page]);

  const fetchMessages = async () => {
    try {
      setLoading(true);
      const params: any = { page, size: 10 };
      if (search) params.search = search;
      if (status) params.status = status;

      const res = await api.get<ApiResponse<PagedResponse<ContactMessage>>>('/api/admin/messages', { params });
      if (res.data.success) {
        setMessages(res.data.data.content);
        setTotalPages(res.data.data.totalPages || 1);
      }
    } catch (err) {
      console.error('Failed to load messages', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenMessage = async (msg: ContactMessage) => {
    setSelectedMessage(msg);
    if (msg.status === 'UNREAD') {
      try {
        await api.patch(`/api/admin/messages/${msg.id}/read`);
        setMessages((prev) =>
          prev.map((m) => (m.id === msg.id ? { ...m, status: 'READ' } : m))
        );
      } catch (err) {
        console.error('Failed to mark read', err);
      }
    }
  };

  const handleArchive = async (id: number) => {
    try {
      await api.patch(`/api/admin/messages/${id}/archive`);
      fetchMessages();
      if (selectedMessage?.id === id) setSelectedMessage(null);
    } catch (err: any) {
      alert(err.message || 'Archive failed');
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Delete this message permanently?')) return;
    try {
      await api.delete(`/api/admin/messages/${id}`);
      fetchMessages();
      if (selectedMessage?.id === id) setSelectedMessage(null);
    } catch (err: any) {
      alert(err.message || 'Delete failed');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Client Inquiries & Messages</h1>
          <p className="text-sm text-slate-400 mt-1">Review contact form inquiries, recruiting requests, and project leads</p>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="flex flex-col sm:flex-row gap-3 bg-slate-900 p-4 rounded-xl border border-slate-800">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            placeholder="Search by name, email, or subject..."
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
          <option value="">All Inquiries</option>
          <option value="UNREAD">Unread Only</option>
          <option value="READ">Read</option>
          <option value="ARCHIVED">Archived</option>
        </select>
      </div>

      {/* Table */}
      <div className="bg-slate-900 border border-slate-800 rounded-2xl overflow-hidden shadow-xl">
        {loading ? (
          <div className="p-12 text-center text-slate-500">Loading messages...</div>
        ) : messages.length === 0 ? (
          <div className="p-12 text-center text-slate-500">
            <Mail className="w-10 h-10 mx-auto mb-2 text-slate-600" />
            <p className="text-base font-medium text-slate-400">Inbox is empty</p>
            <p className="text-sm mt-1">New contact form messages submitted from the public site will appear here.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-950/60 border-b border-slate-800 text-xs font-semibold uppercase text-slate-400">
                <tr>
                  <th className="py-3.5 px-4">Sender</th>
                  <th className="py-3.5 px-4">Subject</th>
                  <th className="py-3.5 px-4">Date</th>
                  <th className="py-3.5 px-4">Status</th>
                  <th className="py-3.5 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800">
                {messages.map((msg) => (
                  <tr
                    key={msg.id}
                    className={`hover:bg-slate-800/40 transition cursor-pointer ${
                      msg.status === 'UNREAD' ? 'bg-indigo-950/20 font-medium' : ''
                    }`}
                    onClick={() => handleOpenMessage(msg)}
                  >
                    <td className="py-4 px-4">
                      <div>
                        <span className={`text-sm ${msg.status === 'UNREAD' ? 'text-white font-bold' : 'text-slate-300'}`}>
                          {msg.name}
                        </span>
                        <p className="text-xs text-slate-400">{msg.email}</p>
                      </div>
                    </td>
                    <td className="py-4 px-4">
                      <p className={`text-sm truncate max-w-sm ${msg.status === 'UNREAD' ? 'text-white' : 'text-slate-300'}`}>
                        {msg.subject}
                      </p>
                      <p className="text-xs text-slate-500 truncate max-w-sm">{msg.message}</p>
                    </td>
                    <td className="py-4 px-4 text-xs text-slate-400 font-mono">
                      {new Date(msg.createdAt).toLocaleDateString()}
                    </td>
                    <td className="py-4 px-4">
                      <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-semibold ${
                        msg.status === 'UNREAD' ? 'bg-indigo-500/20 text-indigo-400 border border-indigo-500/30' :
                        msg.status === 'READ' ? 'bg-slate-800 text-slate-400' :
                        'bg-amber-500/10 text-amber-400'
                      }`}>
                        {msg.status}
                      </span>
                    </td>
                    <td className="py-4 px-4 text-right space-x-1" onClick={(e) => e.stopPropagation()}>
                      <button
                        onClick={() => handleOpenMessage(msg)}
                        title="View message"
                        className="p-1.5 rounded-lg bg-slate-800 text-slate-300 hover:text-white"
                      >
                        <Eye className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={() => handleArchive(msg.id)}
                        title="Archive message"
                        className="p-1.5 rounded-lg bg-slate-800 text-slate-400 hover:text-slate-200"
                      >
                        <Archive className="w-3.5 h-3.5" />
                      </button>
                      <button
                        onClick={() => handleDelete(msg.id)}
                        title="Delete message"
                        className="p-1.5 rounded-lg bg-red-500/10 text-red-400 hover:bg-red-500/20"
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
      </div>

      {/* Message Detail Modal */}
      {selectedMessage && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-xl p-6 shadow-2xl space-y-5">
            <div className="flex justify-between items-start border-b border-slate-800 pb-4">
              <div>
                <h3 className="text-lg font-bold text-white">{selectedMessage.subject}</h3>
                <p className="text-xs text-slate-400 mt-1">
                  From: <span className="text-white font-medium">{selectedMessage.name}</span> ({selectedMessage.email})
                </p>
                <p className="text-[11px] text-slate-500 font-mono mt-0.5">
                  Received: {new Date(selectedMessage.createdAt).toLocaleString()}
                </p>
              </div>
              <button onClick={() => setSelectedMessage(null)} className="text-slate-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="bg-slate-950 border border-slate-800/80 rounded-xl p-4 text-sm text-slate-200 whitespace-pre-wrap leading-relaxed">
              {selectedMessage.message}
            </div>

            <div className="flex justify-between items-center pt-2">
              <button
                onClick={() => handleDelete(selectedMessage.id)}
                className="text-xs text-red-400 hover:text-red-300 flex items-center gap-1.5"
              >
                <Trash2 className="w-3.5 h-3.5" />
                <span>Delete Message</span>
              </button>

              <div className="flex space-x-3">
                <a
                  href={`mailto:${selectedMessage.email}?subject=Re: ${encodeURIComponent(selectedMessage.subject)}`}
                  className="px-4 py-2 bg-indigo-600 hover:bg-indigo-500 text-white rounded-lg text-sm font-medium flex items-center gap-2 shadow-lg shadow-indigo-600/30"
                >
                  <Send className="w-4 h-4" />
                  <span>Reply via Email</span>
                </a>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
