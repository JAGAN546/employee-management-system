import { useState, useEffect } from 'react';
import {
  applyLeave,
  getMyLeaves,
  getMyLeaveBalance,
  cancelLeave,
} from '../services/leaveService';

const LEAVE_TYPES = ['CASUAL', 'SICK', 'EARNED', 'UNPAID', 'MATERNITY', 'PATERNITY'];

const getLeaveStatusBadge = (status) => {
  switch (status) {
    case 'APPROVED':
      return 'badge badge-success';
    case 'PENDING':
      return 'badge badge-warning';
    case 'REJECTED':
    case 'CANCELLED':
      return 'badge badge-error';
    default:
      return 'badge badge-neutral';
  }
};

const LeavePage = () => {
  const [leaves, setLeaves] = useState([]);
  const [balances, setBalances] = useState([]);
  const [loading, setLoading] = useState(true);

  const [leaveType, setLeaveType] = useState('CASUAL');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [reason, setReason] = useState('');

  const [formError, setFormError] = useState('');
  const [formMessage, setFormMessage] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const loadData = async () => {
    try {
      const [leavesData, balanceData] = await Promise.all([
        getMyLeaves(),
        getMyLeaveBalance(),
      ]);
      setLeaves(leavesData);
      setBalances(balanceData);
    } catch (err) {
      setFormError('Failed to load leave data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleApply = async (e) => {
    e.preventDefault();
    setFormError('');
    setFormMessage('');
    setSubmitting(true);

    try {
      await applyLeave({ leaveType, startDate, endDate, reason });
      setFormMessage('Leave applied successfully');
      setStartDate('');
      setEndDate('');
      setReason('');
      await loadData();
    } catch (err) {
      const message = err.response?.data?.message || 'Failed to apply for leave';
      setFormError(message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = async (id) => {
    try {
      await cancelLeave(id);
      await loadData();
    } catch (err) {
      const message = err.response?.data?.message || 'Failed to cancel leave';
      setFormError(message);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1>Leave Management</h1>
        <p>View balances, apply for leave, and track requests</p>
      </div>

      {/* Leave Balance */}
      <div className="card mb-6">
        <div className="card-header">
          <h3 className="card-title">Leave Balance</h3>
        </div>

        {loading ? (
          <div className="loading-state">Loading...</div>
        ) : balances.length === 0 ? (
          <div className="empty-state">No leave balance data.</div>
        ) : (
          <div className="stats-grid">
            {balances.map((b) => (
              <div className="stat-card" key={b.leaveType}>
                <div className="stat-label">{b.leaveType}</div>
                <div className="stat-value">
                  {b.remainingDays}
                  <span style={{ fontSize: '14px', fontWeight: 400, color: 'var(--text-secondary)' }}>
                    {' '}/ {b.totalDays} days
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Apply form + Requests */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '24px' }}>
        {/* Apply for Leave */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Apply for Leave</h3>
          </div>

          <form onSubmit={handleApply}>
            <div className="form-group">
              <label className="form-label">Leave Type</label>
              <select
                className="form-select"
                value={leaveType}
                onChange={(e) => setLeaveType(e.target.value)}
              >
                {LEAVE_TYPES.map((type) => (
                  <option key={type} value={type}>{type}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Start Date</label>
              <input
                type="date"
                className="form-input"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">End Date</label>
              <input
                type="date"
                className="form-input"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Reason</label>
              <input
                type="text"
                className="form-input"
                value={reason}
                onChange={(e) => setReason(e.target.value)}
                placeholder="Optional reason"
              />
            </div>

            {formMessage && <div className="alert alert-success">{formMessage}</div>}
            {formError && <div className="alert alert-error">{formError}</div>}

            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? 'Submitting...' : 'Apply'}
            </button>
          </form>
        </div>

        {/* My Leave Requests */}
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">My Leave Requests</h3>
          </div>

          {!loading && leaves.length === 0 && (
            <div className="empty-state">No leave requests yet.</div>
          )}

          {!loading && leaves.length > 0 && (
            <div className="table-container">
              <table className="table">
                <thead>
                  <tr>
                    <th>Type</th>
                    <th>Start</th>
                    <th>End</th>
                    <th>Status</th>
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {leaves.map((leave) => (
                    <tr key={leave.id}>
                      <td>{leave.leaveType}</td>
                      <td>{leave.startDate}</td>
                      <td>{leave.endDate}</td>
                      <td>
                        <span className={getLeaveStatusBadge(leave.status)}>
                          {leave.status}
                        </span>
                      </td>
                      <td>
                        {leave.status === 'PENDING' && (
                          <button
                            className="btn btn-danger btn-sm"
                            onClick={() => handleCancel(leave.id)}
                          >
                            Cancel
                          </button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default LeavePage;