import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const EMPLOYEE_LINKS = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/attendance', label: 'Attendance' },
  { to: '/leaves', label: 'Leave' },
  { to: '/tasks', label: 'Tasks' },
  { to: '/notifications', label: 'Notifications' },
  { to: '/profile', label: 'Profile' },
];

const ADMIN_HR_MANAGER_LINKS = [
  { to: '/dashboard', label: 'Dashboard' },
  { to: '/employees', label: 'Employees' },
  { to: '/notifications', label: 'Notifications' },
];

const Navbar = () => {
  const { user, logoutUser } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logoutUser();
    navigate('/login');
  };

  const links = user?.role === 'EMPLOYEE' ? EMPLOYEE_LINKS : ADMIN_HR_MANAGER_LINKS;

  return (
    <nav
      style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '12px 24px',
        backgroundColor: '#1e293b',
        color: 'white',
        fontFamily: 'sans-serif',
      }}
    >
      <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
        <strong>EMS</strong>
        {links.map((link) => (
          <Link key={link.to} to={link.to} style={{ color: 'white', textDecoration: 'none' }}>
            {link.label}
          </Link>
        ))}
      </div>

      <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
        <span>{user?.email} ({user?.role})</span>
        <button onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
};

export default Navbar;