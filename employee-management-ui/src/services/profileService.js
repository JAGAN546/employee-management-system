import api from './api';

export const getMyProfile = async () => {
  const response = await api.get('/employees/me');
  return response.data;
};

export const updateMyProfile = async (profileData) => {
  const response = await api.put('/employees/me', profileData);
  return response.data;
};