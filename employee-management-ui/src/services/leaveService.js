import api from './api';

export const applyLeave = async (leaveData) => {
  const response = await api.post('/leaves', leaveData);
  return response.data;
};

export const getMyLeaves = async () => {
  const response = await api.get('/leaves/my');
  return response.data;
};

export const getMyLeaveBalance = async () => {
  const response = await api.get('/leaves/my-balance');
  return response.data;
};

export const cancelLeave = async (id) => {
  const response = await api.patch(`/leaves/${id}/cancel`);
  return response.data;
};