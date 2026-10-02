import { useState, useEffect } from 'react';
import {
  applyLeave,
  getMyLeaves,
  getMyLeaveBalance,
  cancelLeave,
} from '../services/leaveService';

const LEAVE_TYPES = ['CASUAL', 'SICK', 'EARNED', 'UNPAID', 'MATERNITY', 'PATERNITY'];

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
    <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
      <h2>Leave Management</h2>

      <h3>Leave Balance</h3>
      {loading && <p>Loading...</p>}
      {!loading && (
        <ul>
          {balances.map((b) => (
            <li key={b.leaveType}>
              {b.leaveType}: {b.remainingDays} / {b.totalDays} days remaining
            </li>
          ))}
        </ul>
      )}

      <h3>Apply for Leave</h3>
      <form onSubmit={handleApply} style={{ maxWidth: '400px' }}>
        <div style={{ marginBottom: '10px' }}>
          <label>Leave Type</label><br />
          <select value={leaveType} onChange={(e) => setLeaveType(e.target.value)}>
            {LEAVE_TYPES.map((type) => (
              <option key={type} value={type}>{type}</option>
            ))}
          </select>
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Start Date</label><br />
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            required
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>End Date</label><br />
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            required
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Reason</label><br />
          <input
            type="text"
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            style={{ width: '100%', padding: '6px' }}
          />
        </div>

        {formMessage && <p style={{ color: 'green' }}>{formMessage}</p>}
        {formError && <p style={{ color: 'red' }}>{formError}</p>}

        <button type="submit" disabled={submitting}>
          {submitting ? 'Submitting...' : 'Apply'}
        </button>
      </form>

      <h3>My Leave Requests</h3>
      {!loading && leaves.length === 0 && <p>No leave requests yet.</p>}
      {!loading && leaves.length > 0 && (
        <table border="1" cellPadding="8" style={{ borderCollapse: 'collapse' }}>
          <thead>
            <tr>
              <th>Type</th>
              <th>Start</th>
              <th>End</th>
              <th>Status</th>
              <th>Reason</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {leaves.map((leave) => (
              <tr key={leave.id}>
                <td>{leave.leaveType}</td>
                <td>{leave.startDate}</td>
                <td>{leave.endDate}</td>
                <td>{leave.status}</td>
                <td>{leave.reason || '—'}</td>
                <td>
                  {leave.status === 'PENDING' && (
                    <button onClick={() => handleCancel(leave.id)}>Cancel</button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default LeavePage;