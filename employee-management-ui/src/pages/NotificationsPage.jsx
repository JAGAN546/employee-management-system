import { useState, useEffect } from 'react';
import {
  getMyNotifications,
  markAsRead,
  markAllAsRead,
} from '../services/notificationService';

const NotificationsPage = () => {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadNotifications = async () => {
    try {
      const data = await getMyNotifications();
      setNotifications(data);
    } catch (err) {
      setError('Failed to load notifications');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNotifications();
  }, []);

  const handleMarkAsRead = async (id) => {
    try {
      await markAsRead(id);
      await loadNotifications();
    } catch (err) {
      setError('Failed to mark notification as read');
    }
  };

  const handleMarkAllAsRead = async () => {
    try {
      await markAllAsRead();
      await loadNotifications();
    } catch (err) {
      setError('Failed to mark all as read');
    }
  };

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div>
      <div className="page-header">
        <h1>Notifications</h1>
        <p>
          {unreadCount > 0
            ? `You have ${unreadCount} unread notification${unreadCount > 1 ? 's' : ''}`
            : 'All caught up'}
        </p>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {unreadCount > 0 && (
        <div className="mb-4">
          <button className="btn btn-secondary btn-sm" onClick={handleMarkAllAsRead}>
            Mark All as Read
          </button>
        </div>
      )}

      <div className="card">
        {loading && <div className="loading-state">Loading notifications...</div>}

        {!loading && notifications.length === 0 && (
          <div className="empty-state">No notifications.</div>
        )}

        {!loading && notifications.length > 0 && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {notifications.map((n) => (
              <div
                key={n.id}
                style={{
                  padding: '16px',
                  borderRadius: '8px',
                  border: '1px solid var(--border)',
                  backgroundColor: n.read ? 'var(--surface)' : 'var(--primary-light)',
                }}
              >
                <div style={{ marginBottom: '6px', fontWeight: n.read ? 400 : 500 }}>
                  {n.message}
                </div>
                <div className="text-secondary" style={{ fontSize: '13px' }}>
                  {n.type} · {new Date(n.createdAt).toLocaleString()}
                </div>
                {!n.read && (
                  <button
                    className="btn btn-secondary btn-sm mt-2"
                    onClick={() => handleMarkAsRead(n.id)}
                  >
                    Mark as Read
                  </button>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default NotificationsPage;