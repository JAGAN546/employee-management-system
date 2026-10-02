import { Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';
import ProtectedRoute from './routes/ProtectedRoute';
import AttendancePage from './pages/AttendancePage';
import LeavePage from './pages/LeavePage';
import TaskPage from './pages/TaskPage';
import EmployeeListPage from './pages/EmployeeListPage';
import RoleProtectedRoute from './routes/RoleProtectedRoute';
import NotificationsPage from './pages/NotificationsPage';
import ProfilePage from './pages/ProfilePage';
import RegisterPage from './pages/RegisterPage';

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to="/dashboard" />} />
      <Route
        path="/attendance"
        element={
          <ProtectedRoute>
            <AttendancePage />
          </ProtectedRoute>
         }
        />
            <Route
              path="/leaves"
              element={
                <ProtectedRoute>
                  <LeavePage />
                </ProtectedRoute>
              }
            />
             <Route
               path="/tasks"
               element={
                   <ProtectedRoute>
                       <TaskPage />
                 </ProtectedRoute>
               }
            />
            <Route
              path="/employees"
              element={
                <RoleProtectedRoute allowedRoles={['ADMIN', 'HR', 'MANAGER']}>
                  <EmployeeListPage />
                </RoleProtectedRoute>
              }
            />
          <Route
            path="/notifications"
            element={
              <ProtectedRoute>
                <NotificationsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <RoleProtectedRoute allowedRoles={['EMPLOYEE']}>
                <ProfilePage />
              </RoleProtectedRoute>
            }
          />
            <Route path="/register" element={<RegisterPage />} />
    </Routes>
  );
}

export default App;