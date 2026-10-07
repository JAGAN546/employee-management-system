import { useState, useEffect } from 'react';
import { checkIn, checkOut, getMyHistory } from '../services/attendanceService';

const getStatusBadge = (status) => {
  if (!status) return 'badge badge-neutral';
  const s = status.toUpperCase();
  if (s === 'PRESENT' || s === 'COMPLETED') return 'badge badge-success';
  if (s === 'LATE' || s === 'HALF_DAY') return 'badge badge-warning';
  if (s === 'ABSENT') return 'badge badge-error';
  return 'badge badge-neutral';
};

const AttendancePage = () => {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionError, setActionError] = useState('');
  const [actionMessage, setActionMessage] = useState('');
  const [actionLoading, setActionLoading] = useState(false);

  const loadHistory = async () => {
    try {
      const data = await getMyHistory();
      setHistory(data);
    } catch (err) {
      setActionError('Failed to load attendance history');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadHistory();
  }, []);

  const handleCheckIn = async () => {
    setActionError('');
    setActionMessage('');
    setActionLoading(true);
    try {
      await checkIn();
      setActionMessage('Checked in successfully');
      await loadHistory();
    } catch (err) {
      const message = err.response?.data?.message || 'Check-in failed';
      setActionError(message);
    } finally {
      setActionLoading(false);
    }
  };

  const handleCheckOut = async () => {
    setActionError('');
    setActionMessage('');
    setActionLoading(true);
    try {
      await checkOut();
      setActionMessage('Checked out successfully');
      await loadHistory();
    } catch (err) {
      const message = err.response?.data?.message || 'Check-out failed';
      setActionError(message);
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div>
      <div className="page-header">
        <h1>Attendance</h1>
        <p>Check in, check out, and view your history</p>
      </div>

      {/* Action buttons */}
      <div className="card mb-6">
        <div className="flex gap-3" style={{ flexWrap: 'wrap' }}>
          <button
            className="btn btn-primary"
            onClick={handleCheckIn}
            disabled={actionLoading}
          >
            {actionLoading ? 'Processing...' : 'Check In'}
          </button>
          <button
            className="btn btn-secondary"
            onClick={handleCheckOut}
            disabled={actionLoading}
          >
            {actionLoading ? 'Processing...' : 'Check Out'}
          </button>
        </div>

        {actionMessage && (
          <div className="alert alert-success mt-4" style={{ marginBottom: 0 }}>
            {actionMessage}
          </div>
        )}
        {actionError && (
          <div className="alert alert-error mt-4" style={{ marginBottom: 0 }}>
            {actionError}
          </div>
        )}
      </div>

      {/* History table */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">Attendance History</h3>
        </div>

        {loading && <div className="loading-state">Loading history...</div>}

        {!loading && history.length === 0 && (
          <div className="empty-state">No attendance records yet.</div>
        )}

        {!loading && history.length > 0 && (
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Check In</th>
                  <th>Check Out</th>
                  <th>Status</th>
                  <th>Working Hours</th>
                </tr>
              </thead>
              <tbody>
                {history.map((record) => (
                  <tr key={record.id}>
                    <td>{record.date}</td>
                    <td>{record.checkInTime || '—'}</td>
                    <td>{record.checkOutTime || '—'}</td>
                    <td>
                      <span className={getStatusBadge(record.status)}>
                        {record.status || '—'}
                      </span>
                    </td>
                    <td>{record.workingHours ?? '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default AttendancePage;