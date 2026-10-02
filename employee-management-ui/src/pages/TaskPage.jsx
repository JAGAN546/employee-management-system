import { useState, useEffect } from 'react';
import { getMyTasks, updateTaskStatus } from '../services/taskService';

const NEXT_STATUS_OPTIONS = {
  TODO: ['IN_PROGRESS'],
  IN_PROGRESS: ['COMPLETED'],
  COMPLETED: [],
  CANCELLED: [],
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
    <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
      <h2>My Tasks</h2>

      {error && <p style={{ color: 'red' }}>{error}</p>}
      {loading && <p>Loading tasks...</p>}
      {!loading && tasks.length === 0 && <p>No tasks assigned.</p>}

      {!loading && tasks.length > 0 && (
        <table border="1" cellPadding="8" style={{ borderCollapse: 'collapse' }}>
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
                  <td>{task.priority}</td>
                  <td>{task.dueDate || '—'}</td>
                  <td>{task.status}</td>
                  <td>
                    {nextOptions.length === 0 && '—'}
                    {nextOptions.map((option) => (
                      <button
                        key={option}
                        onClick={() => handleStatusChange(task.id, option)}
                        disabled={updatingId === task.id}
                        style={{ marginRight: '6px' }}
                      >
                        Mark as {option.replace('_', ' ')}
                      </button>
                    ))}
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default TaskPage;