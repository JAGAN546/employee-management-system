import { useState, useEffect } from 'react';
import { getMyTasks, updateTaskStatus } from '../services/taskService';

const NEXT_STATUS_OPTIONS = {
  TODO: ['IN_PROGRESS'],
  IN_PROGRESS: ['COMPLETED'],
  COMPLETED: [],
  CANCELLED: [],
};

const getStatusBadge = (status) => {
  switch (status) {
    case 'COMPLETED':
      return 'badge badge-success';
    case 'IN_PROGRESS':
      return 'badge badge-info';
    case 'TODO':
      return 'badge badge-warning';
    case 'CANCELLED':
      return 'badge badge-neutral';
    default:
      return 'badge badge-neutral';
  }
};

const getPriorityBadge = (priority) => {
  switch (priority) {
    case 'HIGH':
      return 'badge badge-error';
    case 'MEDIUM':
      return 'badge badge-warning';
    case 'LOW':
      return 'badge badge-neutral';
    default:
      return 'badge badge-neutral';
  }
};

const TaskPage = () => {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [updatingId, setUpdatingId] = useState(null);

  const loadTasks = async () => {
    try {
      const data = await getMyTasks();
      setTasks(data);
    } catch (err) {
      setError('Failed to load tasks');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTasks();
  }, []);

  const handleStatusChange = async (taskId, newStatus) => {
    setError('');
    setUpdatingId(taskId);
    try {
      await updateTaskStatus(taskId, newStatus);
      await loadTasks();
    } catch (err) {
      const message = err.response?.data?.message || 'Failed to update task status';
      setError(message);
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1>My Tasks</h1>
        <p>View and update the status of your assigned tasks</p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      <div className="card">
        {loading && <div className="loading-state">Loading tasks...</div>}

        {!loading && tasks.length === 0 && (
          <div className="empty-state">No tasks assigned.</div>
        )}

        {!loading && tasks.length > 0 && (
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Priority</th>
                  <th>Due Date</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {tasks.map((task) => {
                  const nextOptions = NEXT_STATUS_OPTIONS[task.status] || [];
                  return (
                    <tr key={task.id}>
                      <td>{task.title}</td>
                      <td>
                        <span className={getPriorityBadge(task.priority)}>
                          {task.priority}
                        </span>
                      </td>
                      <td>{task.dueDate || '—'}</td>
                      <td>
                        <span className={getStatusBadge(task.status)}>
                          {task.status}
                        </span>
                      </td>
                      <td>
                        {nextOptions.length === 0 && (
                          <span className="text-muted">—</span>
                        )}
                        {nextOptions.map((option) => (
                          <button
                            key={option}
                            className="btn btn-primary btn-sm"
                            onClick={() => handleStatusChange(task.id, option)}
                            disabled={updatingId === task.id}
                            style={{ marginRight: '6px' }}
                          >
                            {updatingId === task.id
                              ? 'Updating...'
                              : `Mark as ${option.replace('_', ' ')}`}
                          </button>
                        ))}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default TaskPage;