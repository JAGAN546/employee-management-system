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

  if (loading) {
    return <div className="loading-state">Loading dashboard...</div>;
  }

  if (error) {
    return <div className="alert alert-error">{error}</div>;
  }

  return (
    <div>
      <div className="page-header">
        <h1>Welcome back</h1>
        <p>{user?.email}</p>
      </div>

      {/* ========== EMPLOYEE DASHBOARD ========== */}
      {dashboard && isEmployee && (
        <>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-label">Checked in today</div>
              <div className="stat-value">
                {dashboard.checkedInToday ? 'Yes' : 'No'}
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-label">Check-in time</div>
              <div className="stat-value" style={{ fontSize: '18px' }}>
                {dashboard.todayCheckInTime || '—'}
              </div>
            </div>

            <div className="stat-card">
              <div className="stat-label">Pending leave requests</div>
              <div className="stat-value">{dashboard.pendingLeaveCount}</div>
            </div>

            <div className="stat-card">
              <div className="stat-label">Assigned tasks</div>
              <div className="stat-value">{dashboard.totalAssignedTasks}</div>
            </div>

            <div className="stat-card">
              <div className="stat-label">Completed tasks</div>
              <div className="stat-value">{dashboard.completedTasks}</div>
            </div>

            <div className="stat-card">
              <div className="stat-label">Unread notifications</div>
              <div className="stat-value">{dashboard.unreadNotificationCount}</div>
            </div>
          </div>

          <div className="card">
            <div className="card-header">
              <h3 className="card-title">Leave Balances</h3>
            </div>

            {dashboard.leaveBalances && dashboard.leaveBalances.length > 0 ? (
              <div className="table-container">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Leave Type</th>
                      <th>Remaining</th>
                      <th>Total</th>
                    </tr>
                  </thead>
                  <tbody>
                    {dashboard.leaveBalances.map((balance) => (
                      <tr key={balance.leaveType}>
                        <td>{balance.leaveType}</td>
                        <td>{balance.remainingDays} days</td>
                        <td>{balance.totalDays} days</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="empty-state">No leave balance data available.</div>
            )}
          </div>
        </>
      )}

      {/* ========== ADMIN / HR / MANAGER DASHBOARD ========== */}
      {dashboard && !isEmployee && (
        <>
          <div className="stats-grid">
            <div className="stat-card">
              <div className="stat-label">Total Employees</div>
              <div className="stat-value">{dashboard.totalEmployees}</div>
            </div>

            <div className="stat-card">
              <div className="stat-label">Active Employees</div>
              <div className="stat-value">{dashboard.activeEmployees}</div>
            </div>

            <div className="stat-card">
              <div className="stat-label">Departments</div>
              <div className="stat-value">{dashboard.totalDepartments}</div>
            </div>

            <div className="stat-card">
              <div className="stat-label">On Leave Today</div>
              <div className="stat-value">{dashboard.employeesOnLeaveToday}</div>
            </div>

            <div className="stat-card">
              <div className="stat-label">Today's Attendance</div>
              <div className="stat-value">{dashboard.todaysAttendanceCount}</div>
            </div>

            <div className="stat-card">
              <div className="stat-label">Pending Leave Requests</div>
              <div className="stat-value">{dashboard.pendingLeaveRequests}</div>
            </div>
          </div>

          <div className="card">
            <div className="card-header">
              <h3 className="card-title">Recently Added Employees</h3>
            </div>

            {dashboard.recentEmployees && dashboard.recentEmployees.length > 0 ? (
              <div className="table-container">
                <table className="table">
                  <thead>
                    <tr>
                      <th>Employee Code</th>
                      <th>Name</th>
                    </tr>
                  </thead>
                  <tbody>
                    {dashboard.recentEmployees.map((emp) => (
                      <tr key={emp.id}>
                        <td>{emp.employeeCode}</td>
                        <td>{emp.firstName} {emp.lastName}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="empty-state">No recent employees.</div>
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default DashboardPage;