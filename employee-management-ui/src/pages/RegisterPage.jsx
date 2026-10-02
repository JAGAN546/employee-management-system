import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { register } from '../services/authService';

const RegisterPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await register(email, password);
      setSuccess(true);
      setTimeout(() => navigate('/login'), 1500);
    } catch (err) {
      const message = err.response?.data?.message || 'Registration failed';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '80px auto', fontFamily: 'sans-serif' }}>
      <h2>Employee Management System</h2>
      <h3>Register</h3>
      <p style={{ fontSize: '14px', color: '#555' }}>
        Use the same email address HR used when adding you to the system.
      </p>

      {success ? (
        <p style={{ color: 'green' }}>
          Registration successful. Redirecting to login...
        </p>
      ) : (
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '12px' }}>
            <label>Email</label><br />
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              style={{ width: '100%', padding: '8px' }}
            />
          </div>

          <div style={{ marginBottom: '12px' }}>
            <label>Password</label><br />
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              minLength={6}
              style={{ width: '100%', padding: '8px' }}
            />
          </div>

          {error && <p style={{ color: 'red' }}>{error}</p>}

          <button type="submit" disabled={loading} style={{ padding: '8px 16px' }}>
            {loading ? 'Registering...' : 'Register'}
          </button>
        </form>
      )}

      <p style={{ marginTop: '16px' }}>
        Already have an account? <Link to="/login">Login</Link>
      </p>
    </div>
  );
};

export default RegisterPage;