import api from './api';

export const checkIn = async () => {
  const response = await api.post('/attendance/check-in');
  return response.data;
};

export const checkOut = async () => {
  const response = await api.post('/attendance/check-out');
  return response.data;
};

export const getMyHistory = async () => {
  const response = await api.get('/attendance/my-history');
  return response.data;
};