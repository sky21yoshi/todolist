import { useEffect, useMemo, useState } from 'react'
import { Archive, CalendarDays, Check, CircleHelp, ChevronDown, Filter, Hash, Inbox, ListFilter, MoreHorizontal, PanelLeftClose, PanelLeftOpen, Plus, Search, Settings2, Sun, Tag, Trash2, X } from 'lucide-react'
import './App.css'

type Priority = '高' | '中' | '低'
type Task = { id: number; title: string; category: string; tags: string[]; priority: Priority; due: string; completed: boolean }
const seedTasks: Task[] = [
  { id: 1, title: 'API のエラーレスポンスを整理する', category: '仕事', tags: ['開発', '今週'], priority: '高', due: '今日 17:00', completed: false },
  { id: 2, title: '来週のスプリント計画を確認', category: '仕事', tags: ['計画'], priority: '中', due: '明日', completed: false },
  { id: 3, title: '食材を買い出しする', category: '個人', tags: ['買い物'], priority: '低', due: '9月5日', completed: false },
  { id: 4, title: 'README のセットアップ手順を更新', category: '仕事', tags: ['ドキュメント'], priority: '低', due: '9月6日', completed: false },
  { id: 5, title: 'デザインレビューのフィードバックを反映', category: '仕事', tags: ['デザイン'], priority: '中', due: '昨日', completed: true },
]

function App() {
  const [tasks, setTasks] = useState<Task[]>(() => { const stored = localStorage.getItem('todolist-tasks'); return stored ? JSON.parse(stored) : seedTasks })
  const [query, setQuery] = useState('')
  const [filter, setFilter] = useState('受信トレイ')
  const [sort, setSort] = useState('期限')
  const [newTitle, setNewTitle] = useState('')
  const [newPriority, setNewPriority] = useState<Priority>('中')
  const [newCategory, setNewCategory] = useState('仕事')
  const [isDark, setIsDark] = useState(false)
  const [sidebarOpen, setSidebarOpen] = useState(true)
  useEffect(() => localStorage.setItem('todolist-tasks', JSON.stringify(tasks)), [tasks])

  const visibleTasks = useMemo(() => {
    const filtered = tasks.filter((task) => {
      const matchesQuery = `${task.title} ${task.category} ${task.tags.join(' ')}`.toLowerCase().includes(query.toLowerCase())
      const matchesFilter = filter === '受信トレイ' ? !task.completed : filter === '完了済み' ? task.completed : filter === '今日' ? task.due.includes('今日') : task.category === filter
      return matchesQuery && matchesFilter
    })
    return [...filtered].sort((a, b) => sort === '優先度' ? ['高', '中', '低'].indexOf(a.priority) - ['高', '中', '低'].indexOf(b.priority) : sort === '作成日' ? b.id - a.id : a.due.localeCompare(b.due))
  }, [filter, query, sort, tasks])
  const addTask = (event: React.FormEvent) => { event.preventDefault(); if (!newTitle.trim()) return; setTasks([{ id: Date.now(), title: newTitle.trim(), category: newCategory, tags: [], priority: newPriority, due: '期限なし', completed: false }, ...tasks]); setNewTitle('') }
  const toggleTask = (id: number) => setTasks(tasks.map((task) => task.id === id ? { ...task, completed: !task.completed } : task))
  const deleteTask = (id: number) => setTasks(tasks.filter((task) => task.id !== id))

  return <div className={`app-shell ${isDark ? 'dark' : ''}`}>
    <header className="topbar"><div className="brand"><span className="brand-mark"><Check size={17} strokeWidth={3} /></span><span>todolist</span></div><div className="topbar-actions"><button className="icon-button mobile-menu" onClick={() => setSidebarOpen(!sidebarOpen)} aria-label="メニュー"><PanelLeftOpen size={19} /></button><button className="icon-button" onClick={() => setIsDark(!isDark)} aria-label="テーマ切替"><Sun size={18} /></button><button className="icon-button" aria-label="ヘルプ"><CircleHelp size={18} /></button><div className="avatar">SK</div></div></header>
    <div className="workspace"><aside className={`sidebar ${sidebarOpen ? '' : 'collapsed'}`}><button className="collapse-button" onClick={() => setSidebarOpen(false)} aria-label="サイドバーを閉じる"><PanelLeftClose size={18} /></button><nav><p className="nav-label">WORKSPACE</p><NavButton active={filter === '受信トレイ'} onClick={() => setFilter('受信トレイ')}><Inbox size={17} />受信トレイ<span>{tasks.filter((task) => !task.completed).length}</span></NavButton><NavButton active={filter === '今日'} onClick={() => setFilter('今日')}><CalendarDays size={17} />今日</NavButton><NavButton active={filter === '完了済み'} onClick={() => setFilter('完了済み')}><Archive size={17} />完了済み</NavButton><p className="nav-label second">COLLECTIONS <Plus size={14} /></p>{['仕事', '個人'].map((name) => <NavButton key={name} active={filter === name} onClick={() => setFilter(name)}><span className={`category-dot ${name === '仕事' ? 'coral' : 'mint'}`} />{name}<span>{tasks.filter((task) => task.category === name && !task.completed).length}</span></NavButton>)}<p className="nav-label second">TAGS</p><NavButton><Hash size={17} />開発</NavButton><NavButton><Tag size={17} />計画</NavButton></nav><div className="sidebar-footer"><NavButton><Settings2 size={17} />設定</NavButton><NavButton><Trash2 size={17} />ゴミ箱</NavButton></div></aside>
      <main className="main-content"><div className="content-heading"><div><p className="eyebrow">{filter === '受信トレイ' ? 'OVERVIEW' : 'COLLECTION'}</p><h1>{filter}</h1><p className="subtitle">{filter === '受信トレイ' ? '今日も少しずつ、前へ進めよう。' : `${visibleTasks.length} 件のタスク`}</p></div><div className="heading-date"><span>2026年</span><strong>09 / 03</strong><span>木曜日</span></div></div>
        <form className="quick-add" onSubmit={addTask}><Plus size={20} /><input value={newTitle} onChange={(event) => setNewTitle(event.target.value)} placeholder="新しいタスクを追加..." aria-label="新しいタスク" /><div className="quick-options"><select value={newPriority} onChange={(event) => setNewPriority(event.target.value as Priority)} aria-label="優先度"><option>高</option><option>中</option><option>低</option></select><select value={newCategory} onChange={(event) => setNewCategory(event.target.value)} aria-label="カテゴリ"><option>仕事</option><option>個人</option></select><button type="submit" className="add-button">追加 <span>↵</span></button></div></form>
        <div className="toolbar"><div className="search-box"><Search size={17} /><input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="タスクを検索" />{query && <button onClick={() => setQuery('')} aria-label="検索をクリア"><X size={15} /></button>}</div><div className="toolbar-right"><div className="select-wrap"><ListFilter size={15} /><select value={sort} onChange={(event) => setSort(event.target.value)} aria-label="並び替え"><option>期限</option><option>優先度</option><option>作成日</option></select><ChevronDown size={14} /></div><button className="filter-button"><Filter size={16} />絞り込み</button></div></div>
        <TaskSection title="進行中" tasks={visibleTasks.filter((task) => !task.completed)} onToggle={toggleTask} onDelete={deleteTask} /><TaskSection title="完了済み" tasks={visibleTasks.filter((task) => task.completed)} onToggle={toggleTask} onDelete={deleteTask} completed />{visibleTasks.length === 0 && <div className="empty-state"><div className="empty-icon"><Check size={24} /></div><h2>ここは静かです</h2><p>検索条件を変えるか、新しいタスクを追加しましょう。</p></div>}
      </main></div></div>
}
function NavButton({ active = false, onClick, children }: { active?: boolean; onClick?: () => void; children: React.ReactNode }) { return <button className={`nav-item ${active ? 'active' : ''}`} onClick={onClick}>{children}</button> }
function TaskSection({ title, tasks, onToggle, onDelete, completed = false }: { title: string; tasks: Task[]; onToggle: (id: number) => void; onDelete: (id: number) => void; completed?: boolean }) { return <div className={`task-section ${completed ? 'completed-section' : ''}`}><div className="section-header"><span>{title} <b>{tasks.length}</b></span><button className="text-button">{completed ? '折りたたむ' : 'すべて表示'} <ChevronDown size={14} /></button></div><div className="task-list">{tasks.map((task) => <TaskRow key={task.id} task={task} onToggle={onToggle} onDelete={onDelete} />)}</div></div> }
function TaskRow({ task, onToggle, onDelete }: { task: Task; onToggle: (id: number) => void; onDelete: (id: number) => void }) { return <article className={`task-row ${task.completed ? 'completed' : ''}`}><button className="check-button" onClick={() => onToggle(task.id)} aria-label={`${task.title}を${task.completed ? '未完了' : '完了'}にする`}>{task.completed && <Check size={14} strokeWidth={3} />}</button><div className="task-copy"><h3>{task.title}</h3><div className="task-meta"><span className="category-label"><span className="category-dot coral" />{task.category}</span>{task.tags.map((tag) => <span className="tag" key={tag}>#{tag}</span>)}</div></div><div className={`priority ${task.priority === '高' ? 'high' : task.priority === '中' ? 'medium' : 'low'}`}><span />{task.priority}</div><div className="due-date"><CalendarDays size={14} />{task.due}</div><button className="more-button" onClick={() => onDelete(task.id)} aria-label="削除"><MoreHorizontal size={18} /></button></article> }
export default App
