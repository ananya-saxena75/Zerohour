import React, { useState, useEffect } from 'react';

export default function App() {
  const [tasks, setTasks] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    title: '', description: '', deadline: '', estimatedHours: '', importance: 5
  });

  useEffect(() => { loadTasks(); }, []);

  const loadTasks = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/tasks');
      const data = await res.json();
      setTasks(Array.isArray(data) ? data : []);
    } catch (err) { setTasks([]); }
  };

  const handleCreate = async () => {
    if (!form.title) { alert('Enter a title!'); return; }
    await fetch('http://localhost:8080/api/tasks', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: form.title, description: form.description || '',
        deadline: form.deadline || null,
        estimatedHours: parseFloat(form.estimatedHours) || 0,
        importance: form.importance, userId: 1,
        status: 'PENDING', postponedCount: 0, completionPercent: 0
      })
    });
    setShowForm(false);
    setForm({ title: '', description: '', deadline: '', estimatedHours: '', importance: 5 });
    loadTasks();
  };
  const connectCalendar = async () => {
  const res = await fetch('http://localhost:8080/api/calendar/auth-url');
  const data = await res.json();
  window.open(data.url, '_blank', 'width=500,height=600');
};

  const getUrgencyColor = (deadline) => {
    if (!deadline) return '#a78bfa';
    const hours = (new Date(deadline) - new Date()) / 36e5;
    if (hours < 6) return '#f87171';
    if (hours < 24) return '#fbbf24';
    return '#a78bfa';
  };

  const getHoursLeft = (deadline) => {
    if (!deadline) return null;
    const hours = Math.floor((new Date(deadline) - new Date()) / 36e5);
    if (hours < 0) return '⚠️ OVERDUE';
    if (hours < 24) return `⚡ ${hours}h left`;
    return `✨ ${Math.floor(hours / 24)}d left`;
  };

  const atRisk = tasks.filter(t => {
    if (!t.deadline) return false;
    const h = (new Date(t.deadline) - new Date()) / 36e5;
    return h < 24 && h > 0;
  }).length;

  

  return (
    <div style={{ minHeight: '100vh', background: 'linear-gradient(135deg, #1a0533 0%, #2d1b69 50%, #1a0533 100%)', color: 'white', fontFamily: "'Segoe UI', sans-serif" }}>

      {/* Header */}
      <div style={{ padding: '24px 40px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #ffffff15', backdropFilter: 'blur(10px)', background: '#ffffff08' }}>
        <div>
          <h1 style={{ margin: 0, fontSize: 32, fontWeight: 900, background: 'linear-gradient(90deg, #c084fc, #818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
            ⚡ ZeroHour
          </h1>
          <p style={{ margin: 0, fontSize: 12, color: '#a78bfa', letterSpacing: 2, textTransform: 'uppercase' }}>AI Deadline Guardian</p>
        </div>
          <div style={{ display: 'flex', gap: 12 }}>
    <button onClick={connectCalendar} style={{
      background: 'linear-gradient(135deg, #059669, #047857)',
      color: 'white', border: 'none', padding: '12px 20px',
      borderRadius: 50, cursor: 'pointer', fontSize: 14,
      fontWeight: 'bold'
    }}>
      📅 Connect Calendar
    </button>
        <button onClick={() => setShowForm(true)} style={{
          background: 'linear-gradient(135deg, #7c3aed, #4f46e5)',
          color: 'white', border: 'none', padding: '12px 28px',
          borderRadius: 50, cursor: 'pointer', fontSize: 14,
          fontWeight: 'bold', boxShadow: '0 4px 20px #7c3aed55',
          letterSpacing: 1
        }}>
          + New Task
        </button>
      </div>

      <div style={{ padding: '32px 40px', maxWidth: 960, margin: '0 auto' }}>

        {/* Stats Grid */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4,1fr)', gap: 16, marginBottom: 40 }}>
          {[
            { label: 'Total Tasks', value: tasks.length, icon: '📋', color: '#818cf8' },
            { label: 'Pending', value: tasks.filter(t => t.status === 'PENDING').length, icon: '⏳', color: '#c084fc' },
            { label: 'Completed', value: tasks.filter(t => t.status === 'DONE').length, icon: '✅', color: '#4ade80' },
            { label: 'At Risk', value: atRisk, icon: '🚨', color: '#f87171' },
          ].map(s => (
            <div key={s.label} style={{
              background: '#ffffff0d', borderRadius: 16, padding: '24px 20px',
              textAlign: 'center', border: '1px solid #ffffff15',
              backdropFilter: 'blur(10px)',
              transition: 'transform 0.2s',
            }}>
              <div style={{ fontSize: 28, marginBottom: 8 }}>{s.icon}</div>
              <div style={{ fontSize: 36, fontWeight: 900, color: s.color, lineHeight: 1 }}>{s.value}</div>
              <div style={{ color: '#a78bfa', fontSize: 12, marginTop: 6, letterSpacing: 1 }}>{s.label}</div>
            </div>
          ))}
        </div>

        {/* Section Title */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
          <h2 style={{ margin: 0, fontSize: 20, color: '#e2e8f0', fontWeight: 700 }}>My Tasks</h2>
          <span style={{ color: '#a78bfa', fontSize: 13 }}>{tasks.length} tasks total</span>
        </div>

        {/* Task Cards */}
        {tasks.length === 0 ? (
          <div style={{ background: '#ffffff08', borderRadius: 20, padding: 60, textAlign: 'center', border: '1px dashed #ffffff25' }}>
            <div style={{ fontSize: 48, marginBottom: 16 }}>✨</div>
            <p style={{ color: '#a78bfa', fontSize: 16 }}>No tasks yet. Start by adding one!</p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            {tasks.map((task, i) => (
              <div key={task.id} style={{
                background: 'linear-gradient(135deg, #ffffff0d, #ffffff05)',
                borderRadius: 16, padding: '20px 24px',
                border: '1px solid #ffffff15',
                borderLeft: `4px solid ${getUrgencyColor(task.deadline)}`,
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                backdropFilter: 'blur(10px)',
              }}>
                <div style={{ flex: 1 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 6 }}>
                    <span style={{ color: '#6366f1', fontSize: 12, fontWeight: 'bold' }}>#{i + 1}</span>
                    <span style={{ fontWeight: 700, fontSize: 16, color: '#f1f5f9' }}>{task.title}</span>
                  </div>
                  {task.description && (
                    <p style={{ color: '#94a3b8', fontSize: 13, margin: '0 0 10px' }}>{task.description}</p>
                  )}
                  <div style={{ display: 'flex', gap: 16, flexWrap: 'wrap' }}>
                    {task.deadline && (
                      <span style={{ color: '#fbbf24', fontSize: 12, display: 'flex', alignItems: 'center', gap: 4 }}>
                        🕐 {new Date(task.deadline).toLocaleString()}
                      </span>
                    )}
                    {task.estimatedHours > 0 && (
                      <span style={{ color: '#94a3b8', fontSize: 12 }}>⏱ {task.estimatedHours}h</span>
                    )}
                    <span style={{ color: '#c084fc', fontSize: 12 }}>⭐ {task.importance}/10</span>
                  </div>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 8, marginLeft: 16 }}>
                  {task.deadline && (
                    <span style={{
                      background: getUrgencyColor(task.deadline) + '22',
                      color: getUrgencyColor(task.deadline),
                      padding: '5px 12px', borderRadius: 20,
                      fontSize: 12, fontWeight: 'bold', whiteSpace: 'nowrap'
                    }}>
                      {getHoursLeft(task.deadline)}
                    </span>
                  )}
                  <span style={{
                    background: '#ffffff10', color: '#a78bfa',
                    padding: '4px 12px', borderRadius: 20, fontSize: 11
                  }}>
                    {task.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Modal */}
      {showForm && (
        <div style={{ position: 'fixed', inset: 0, background: '#00000088', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 100, backdropFilter: 'blur(4px)' }}>
          <div style={{ background: 'linear-gradient(135deg, #2d1b69, #1e1057)', borderRadius: 24, padding: 36, width: 460, maxWidth: '90vw', border: '1px solid #ffffff20', boxShadow: '0 25px 50px #00000088' }}>
            <h2 style={{ margin: '0 0 24px', fontSize: 22, background: 'linear-gradient(90deg, #c084fc, #818cf8)', WebkitBackgroundClip: 'text', WebkitTextFillColor: 'transparent' }}>
              ✨ New Task
            </h2>

            {[
              { key: 'title', placeholder: 'What needs to be done? *', type: 'text' },
              { key: 'description', placeholder: 'Add details...', type: 'text' },
              { key: 'estimatedHours', placeholder: 'Estimated hours (e.g. 2.5)', type: 'number' },
            ].map(f => (
              <input key={f.key} type={f.type} placeholder={f.placeholder} value={form[f.key]}
                onChange={e => setForm({ ...form, [f.key]: e.target.value })}
                style={{ width: '100%', padding: '12px 16px', marginBottom: 12, background: '#ffffff10', border: '1px solid #ffffff20', borderRadius: 12, color: 'white', fontSize: 14, boxSizing: 'border-box', outline: 'none' }} />
            ))}

            <input type="datetime-local" value={form.deadline}
              onChange={e => setForm({ ...form, deadline: e.target.value })}
              style={{ width: '100%', padding: '12px 16px', marginBottom: 16, background: '#ffffff10', border: '1px solid #ffffff20', borderRadius: 12, color: 'white', fontSize: 14, boxSizing: 'border-box' }} />

            <div style={{ marginBottom: 24 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                <label style={{ color: '#a78bfa', fontSize: 13 }}>Importance</label>
                <span style={{ color: '#c084fc', fontWeight: 'bold' }}>{form.importance}/10</span>
              </div>
              <input type="range" min="1" max="10" value={form.importance}
                onChange={e => setForm({ ...form, importance: parseInt(e.target.value) })}
                style={{ width: '100%', accentColor: '#7c3aed' }} />
            </div>

            <div style={{ display: 'flex', gap: 12 }}>
              <button onClick={handleCreate} style={{
                flex: 1, background: 'linear-gradient(135deg, #7c3aed, #4f46e5)',
                color: 'white', border: 'none', padding: 14, borderRadius: 12,
                cursor: 'pointer', fontWeight: 'bold', fontSize: 15,
                boxShadow: '0 4px 15px #7c3aed44'
              }}>
                Save Task ✨
              </button>
              <button onClick={() => setShowForm(false)} style={{
                flex: 1, background: '#ffffff10', color: '#a78bfa',
                border: '1px solid #ffffff20', padding: 14, borderRadius: 12,
                cursor: 'pointer', fontSize: 15
              }}>
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}
    </div>

  </div>
  );
}
