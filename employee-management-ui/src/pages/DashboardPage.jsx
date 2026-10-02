import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { getEmployeeDashboard, getAdminDashboard } from '../services/dashboardService';

const DashboardPage = () => {
  const { user } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const isEmployee = user?.role === 'EMPLOYEE';

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const data = isEmployee ? await getEmployeeDashboard() : await getAdminDashboard();
        setDashboard(data);
      } catch (err) {
        setError('Failed to load dashboard');
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, [isEmployee]);

  return (
    <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
      <h2>Dashboard</h2>
      <p>Welcome, {user?.email}</p>

      {loading && <p>Loading dashboard...</p>}
      {error && <p style={{ color: 'red' }}>{error}</p>}

      {dashboard && isEmployee && (
        <div>
          <p>Checked in today: {dashboard.checkedInToday ? 'Yes' : 'No'}</p>
          <p>Check-in time: {dashboard.todayCheckInTime || '—'}</p>
          <p>Pending leave requests: {dashboard.pendingLeaveCount}</p>
          <p>Total assigned tasks: {dashboard.totalAssignedTasks}</p>
          <p>Completed tasks: {dashboard.completedTasks}</p>
          <p>Unread notifications: {dashboard.unreadNotificationCount}</p>

          <h3>Leave Balances</h3>
          <ul>
            {dashboard.leaveBalances.map((balance) => (
              <li key={balance.leaveType}>
                {balance.leaveType}: {balance.remainingDays} / {balance.totalDays} days remaining
              </li>
            ))}
          </ul>
        </div>
      )}

      {dashboard && !isEmployee && (
        <div>
          <p>Total Employees: {dashboard.totalEmployees}</p>
          <p>Active Employees: {dashboard.activeEmployees}</p>
          <p>Total Departments: {dashboard.totalDepartments}</p>
          <p>Employees on Leave Today: {dashboard.employeesOnLeaveToday}</p>
          <p>Today's Attendance Count: {dashboard.todaysAttendanceCount}</p>
          <p>Pending Leave Requests: {dashboard.pendingLeaveRequests}</p>

          <h3>Recently Added Employees</h3>
          <ul>
            {dashboard.recentEmployees.map((emp) => (
              <li key={emp.id}>
                {emp.employeeCode} — {emp.firstName} {emp.lastName}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default DashboardPage;