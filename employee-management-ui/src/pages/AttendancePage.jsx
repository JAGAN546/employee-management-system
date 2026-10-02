import { useState, useEffect } from 'react';
import { checkIn, checkOut, getMyHistory } from '../services/attendanceService';

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
    <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
      <h2>Attendance</h2>

      <div style={{ marginBottom: '20px' }}>
        <button onClick={handleCheckIn} disabled={actionLoading} style={{ marginRight: '10px', padding: '8px 16px' }}>
          Check In
        </button>
        <button onClick={handleCheckOut} disabled={actionLoading} style={{ padding: '8px 16px' }}>
          Check Out
        </button>
      </div>

      {actionMessage && <p style={{ color: 'green' }}>{actionMessage}</p>}
      {actionError && <p style={{ color: 'red' }}>{actionError}</p>}

      <h3>History</h3>

      {loading && <p>Loading history...</p>}

      {!loading && history.length === 0 && <p>No attendance records yet.</p>}

      {!loading && history.length > 0 && (
        <table border="1" cellPadding="8" style={{ borderCollapse: 'collapse' }}>
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
                <td>{record.status || '—'}</td>
                <td>{record.workingHours ?? '—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default AttendancePage;