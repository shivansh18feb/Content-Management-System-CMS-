import React, { useEffect, useState } from 'react';
import { Plus, Trash2, Edit2, Wrench, FolderPlus, X, Star } from 'lucide-react';
import { api } from '../services/api';
import { ApiResponse, Skill, SkillCategory } from '../types';

export const Skills: React.FC = () => {
  const [categories, setCategories] = useState<{ category: SkillCategory; skills: Skill[] }[]>([]);
  const [flatCategories, setFlatCategories] = useState<SkillCategory[]>([]);
  const [loading, setLoading] = useState(true);

  // Modals
  const [isSkillModalOpen, setIsSkillModalOpen] = useState(false);
  const [isCategoryModalOpen, setIsCategoryModalOpen] = useState(false);
  const [editingSkillId, setEditingSkillId] = useState<number | null>(null);

  const [skillForm, setSkillForm] = useState({
    name: '',
    icon: '',
    proficiency: 85,
    yearsOfExperience: 3,
    categoryId: undefined as number | undefined,
    displayOrder: 0,
    featured: false,
    active: true,
  });

  const [categoryForm, setCategoryForm] = useState({
    name: '',
    displayOrder: 0,
  });

  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      const [groupedRes, catRes] = await Promise.all([
        api.get<ApiResponse<{ category: SkillCategory; skills: Skill[] }[]>>('/api/public/skills'),
        api.get<ApiResponse<SkillCategory[]>>('/api/admin/skills/categories'),
      ]);

      if (groupedRes.data.success) {
        setCategories(groupedRes.data.data);
      }
      if (catRes.data.success) {
        setFlatCategories(catRes.data.data);
      }
    } catch (err) {
      console.error('Failed to load skills', err);
    } finally {
      setLoading(false);
    }
  };

  const handleOpenCreateSkill = (defaultCategoryId?: number) => {
    setEditingSkillId(null);
    setSkillForm({
      name: '',
      icon: '',
      proficiency: 85,
      yearsOfExperience: 2,
      categoryId: defaultCategoryId || flatCategories[0]?.id,
      displayOrder: 0,
      featured: false,
      active: true,
    });
    setIsSkillModalOpen(true);
  };

  const handleOpenEditSkill = (skill: Skill) => {
    setEditingSkillId(skill.id);
    setSkillForm({
      name: skill.name,
      icon: skill.icon || '',
      proficiency: skill.proficiency,
      yearsOfExperience: skill.yearsOfExperience || 1,
      categoryId: skill.category?.id,
      displayOrder: skill.displayOrder,
      featured: skill.featured,
      active: skill.active,
    });
    setIsSkillModalOpen(true);
  };

  const handleSaveSkill = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      if (editingSkillId) {
        await api.put(`/api/admin/skills/${editingSkillId}`, skillForm);
      } else {
        await api.post('/api/admin/skills', skillForm);
      }
      setIsSkillModalOpen(false);
      loadData();
    } catch (err: any) {
      alert(err.response?.data?.message || err.message || 'Failed to save skill');
    } finally {
      setSaving(false);
    }
  };

  const handleSaveCategory = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await api.post('/api/admin/skills/categories', categoryForm);
      setIsCategoryModalOpen(false);
      setCategoryForm({ name: '', displayOrder: 0 });
      loadData();
    } catch (err: any) {
      alert(err.response?.data?.message || err.message || 'Failed to create category');
    } finally {
      setSaving(false);
    }
  };

  const handleDeleteSkill = async (id: number) => {
    if (!window.confirm('Delete this skill?')) return;
    try {
      await api.delete(`/api/admin/skills/${id}`);
      loadData();
    } catch (err: any) {
      alert(err.message || 'Delete failed');
    }
  };

  const handleDeleteCategory = async (id: number) => {
    if (!window.confirm('Delete this category? Associated skills will become uncategorized.')) return;
    try {
      await api.delete(`/api/admin/skills/categories/${id}`);
      loadData();
    } catch (err: any) {
      alert(err.message || 'Delete failed');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Skills & Tech Stack</h1>
          <p className="text-sm text-slate-400 mt-1">Organize your technologies, frameworks, and proficiency levels</p>
        </div>
        <div className="flex space-x-3">
          <button
            onClick={() => setIsCategoryModalOpen(true)}
            className="flex items-center space-x-2 bg-slate-800 hover:bg-slate-700 text-slate-200 font-medium px-3.5 py-2 rounded-xl transition border border-slate-700"
          >
            <FolderPlus className="w-4 h-4" />
            <span>New Category</span>
          </button>
          <button
            onClick={() => handleOpenCreateSkill()}
            className="flex items-center space-x-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium px-4 py-2 rounded-xl transition shadow-lg shadow-indigo-600/30"
          >
            <Plus className="w-4 h-4" />
            <span>Add Skill</span>
          </button>
        </div>
      </div>

      {/* Grouped Skills View */}
      {loading ? (
        <div className="p-12 text-center text-slate-500">
          <div className="w-6 h-6 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin mx-auto mb-2"></div>
          Loading skills...
        </div>
      ) : categories.length === 0 ? (
        <div className="p-12 text-center text-slate-500 bg-slate-900 border border-slate-800 rounded-2xl">
          <Wrench className="w-10 h-10 mx-auto mb-2 text-slate-600" />
          <p className="text-base font-medium text-slate-400">No skills defined</p>
          <p className="text-sm mt-1">Add a category or skill to begin.</p>
        </div>
      ) : (
        <div className="space-y-6">
          {categories.map((group) => (
            <div key={group.category.id} className="bg-slate-900 border border-slate-800 rounded-2xl p-6">
              <div className="flex justify-between items-center mb-5 pb-3 border-b border-slate-800">
                <div className="flex items-center space-x-3">
                  <h3 className="font-bold text-lg text-white">{group.category.name}</h3>
                  <span className="text-xs bg-slate-800 text-slate-400 px-2 py-0.5 rounded-full font-mono">
                    {group.skills.length} skills
                  </span>
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => handleOpenCreateSkill(group.category.id)}
                    className="text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 px-2.5 py-1 rounded-lg transition"
                  >
                    + Add to category
                  </button>
                  <button
                    onClick={() => handleDeleteCategory(group.category.id)}
                    title="Delete category"
                    className="p-1 text-slate-500 hover:text-red-400 transition"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>

              {group.skills.length === 0 ? (
                <p className="text-xs text-slate-500 italic py-2">No skills in this category yet.</p>
              ) : (
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                  {group.skills.map((skill) => (
                    <div
                      key={skill.id}
                      className="bg-slate-950/70 border border-slate-800/80 rounded-xl p-4 flex flex-col justify-between hover:border-slate-700 transition"
                    >
                      <div className="flex justify-between items-start">
                        <div>
                          <div className="flex items-center space-x-2">
                            <span className="font-semibold text-slate-200 text-sm">{skill.name}</span>
                            {skill.featured && (
                              <Star className="w-3.5 h-3.5 text-amber-400 fill-amber-400" />
                            )}
                          </div>
                          {skill.yearsOfExperience && (
                            <span className="text-xs text-slate-500">{skill.yearsOfExperience} yrs exp</span>
                          )}
                        </div>
                        <div className="flex space-x-1">
                          <button
                            onClick={() => handleOpenEditSkill(skill)}
                            className="p-1 text-slate-400 hover:text-white"
                          >
                            <Edit2 className="w-3.5 h-3.5" />
                          </button>
                          <button
                            onClick={() => handleDeleteSkill(skill.id)}
                            className="p-1 text-slate-500 hover:text-red-400"
                          >
                            <Trash2 className="w-3.5 h-3.5" />
                          </button>
                        </div>
                      </div>

                      <div className="mt-3">
                        <div className="flex justify-between text-xs text-slate-400 mb-1">
                          <span>Proficiency</span>
                          <span className="font-mono font-medium text-indigo-400">{skill.proficiency}%</span>
                        </div>
                        <div className="w-full bg-slate-800 h-1.5 rounded-full overflow-hidden">
                          <div
                            className="bg-indigo-500 h-full rounded-full transition-all duration-500"
                            style={{ width: `${skill.proficiency}%` }}
                          />
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Skill Modal */}
      {isSkillModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-md p-6 shadow-2xl">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-bold text-white text-base">
                {editingSkillId ? 'Edit Skill' : 'Add New Skill'}
              </h3>
              <button onClick={() => setIsSkillModalOpen(false)} className="text-slate-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveSkill} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Skill Name *</label>
                <input
                  type="text"
                  required
                  value={skillForm.name}
                  onChange={(e) => setSkillForm({ ...skillForm, name: e.target.value })}
                  placeholder="e.g. Java, Docker, Kubernetes"
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Category</label>
                <select
                  value={skillForm.categoryId || ''}
                  onChange={(e) => setSkillForm({ ...skillForm, categoryId: parseInt(e.target.value) || undefined })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                >
                  <option value="">None (Uncategorized)</option>
                  {flatCategories.map((c) => (
                    <option key={c.id} value={c.id}>
                      {c.name}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <div className="flex justify-between text-xs font-semibold text-slate-300 mb-1">
                  <span>Proficiency ({skillForm.proficiency}%)</span>
                </div>
                <input
                  type="range"
                  min="10"
                  max="100"
                  step="5"
                  value={skillForm.proficiency}
                  onChange={(e) => setSkillForm({ ...skillForm, proficiency: parseInt(e.target.value) })}
                  className="w-full accent-indigo-500 cursor-pointer"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Years of Experience</label>
                  <input
                    type="number"
                    step="0.5"
                    min="0"
                    value={skillForm.yearsOfExperience}
                    onChange={(e) => setSkillForm({ ...skillForm, yearsOfExperience: parseFloat(e.target.value) || 0 })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-300 mb-1">Display Order</label>
                  <input
                    type="number"
                    value={skillForm.displayOrder}
                    onChange={(e) => setSkillForm({ ...skillForm, displayOrder: parseInt(e.target.value) || 0 })}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                  />
                </div>
              </div>

              <div className="flex items-center space-x-4 pt-1">
                <label className="flex items-center space-x-2 text-xs text-slate-300 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={skillForm.featured}
                    onChange={(e) => setSkillForm({ ...skillForm, featured: e.target.checked })}
                    className="rounded bg-slate-950 border-slate-800 text-indigo-600 focus:ring-0"
                  />
                  <span>Featured skill</span>
                </label>
                <label className="flex items-center space-x-2 text-xs text-slate-300 cursor-pointer">
                  <input
                    type="checkbox"
                    checked={skillForm.active}
                    onChange={(e) => setSkillForm({ ...skillForm, active: e.target.checked })}
                    className="rounded bg-slate-950 border-slate-800 text-indigo-600 focus:ring-0"
                  />
                  <span>Active (visible)</span>
                </label>
              </div>

              <div className="flex justify-end space-x-3 pt-4 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsSkillModalOpen(false)}
                  className="px-4 py-2 bg-slate-800 text-slate-300 rounded-lg text-sm"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium rounded-lg text-sm transition"
                >
                  {saving ? 'Saving...' : 'Save Skill'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Category Modal */}
      {isCategoryModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-slate-900 border border-slate-800 rounded-2xl w-full max-w-sm p-6 shadow-2xl">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-bold text-white text-base">New Skill Category</h3>
              <button onClick={() => setIsCategoryModalOpen(false)} className="text-slate-400 hover:text-white">
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleSaveCategory} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Category Name *</label>
                <input
                  type="text"
                  required
                  value={categoryForm.name}
                  onChange={(e) => setCategoryForm({ ...categoryForm, name: e.target.value })}
                  placeholder="e.g. Cloud & DevOps, Databases"
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1">Display Order</label>
                <input
                  type="number"
                  value={categoryForm.displayOrder}
                  onChange={(e) => setCategoryForm({ ...categoryForm, displayOrder: parseInt(e.target.value) || 0 })}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-indigo-500"
                />
              </div>

              <div className="flex justify-end space-x-3 pt-3 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setIsCategoryModalOpen(false)}
                  className="px-4 py-2 bg-slate-800 text-slate-300 rounded-lg text-sm"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={saving}
                  className="px-5 py-2 bg-indigo-600 hover:bg-indigo-500 text-white font-medium rounded-lg text-sm"
                >
                  {saving ? 'Creating...' : 'Create'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
