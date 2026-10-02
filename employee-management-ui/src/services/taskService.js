import api from './api';

export const getMyTasks = async () => {
  const response = await api.get('/tasks/my');
  return response.data;
};

export const updateTaskStatus = async (id, status) => {
  const response = await api.patch(`/tasks/${id}/status`, { status });
  return response.data;
};