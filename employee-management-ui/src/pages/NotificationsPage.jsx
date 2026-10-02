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
    <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
      <h2>Notifications {unreadCount > 0 && `(${unreadCount} unread)`}</h2>

      {unreadCount > 0 && (
        <button onClick={handleMarkAllAsRead} style={{ marginBottom: '16px' }}>
          Mark All as Read
        </button>
      )}

      {error && <p style={{ color: 'red' }}>{error}</p>}
      {loading && <p>Loading notifications...</p>}
      {!loading && notifications.length === 0 && <p>No notifications.</p>}

      {!loading && notifications.length > 0 && (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {notifications.map((n) => (
            <li
              key={n.id}
              style={{
                padding: '12px',
                marginBottom: '8px',
                border: '1px solid #ccc',
                backgroundColor: n.read ? '#f5f5f5' : '#e6f0ff',
              }}
            >
              <div>{n.message}</div>
              <div style={{ fontSize: '12px', color: '#666' }}>
                {n.type} · {new Date(n.createdAt).toLocaleString()}
              </div>
              {!n.read && (
                <button onClick={() => handleMarkAsRead(n.id)} style={{ marginTop: '6px' }}>
                  Mark as Read
                </button>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default NotificationsPage;