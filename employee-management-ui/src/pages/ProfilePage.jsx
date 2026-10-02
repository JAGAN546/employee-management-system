import { useState, useEffect } from 'react';
import { getMyProfile, updateMyProfile } from '../services/profileService';

const ProfilePage = () => {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [phone, setPhone] = useState('');
  const [address, setAddress] = useState('');
  const [gender, setGender] = useState('');
  const [dateOfBirth, setDateOfBirth] = useState('');

  const [formError, setFormError] = useState('');
  const [formMessage, setFormMessage] = useState('');
  const [saving, setSaving] = useState(false);

  const loadProfile = async () => {
    try {
      const data = await getMyProfile();
      setProfile(data);
      setPhone(data.phone || '');
      setAddress(data.address || '');
      setGender(data.gender || '');
      setDateOfBirth(data.dateOfBirth || '');
    } catch (err) {
      setError('Failed to load profile');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadProfile();
  }, []);

  const handleSave = async (e) => {
    e.preventDefault();
    setFormError('');
    setFormMessage('');
    setSaving(true);

    try {
      const updated = await updateMyProfile({ phone, address, gender, dateOfBirth });
      setProfile(updated);
      setFormMessage('Profile updated successfully');
    } catch (err) {
      const message = err.response?.data?.message || 'Failed to update profile';
      setFormError(message);
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div style={{ padding: '40px' }}>Loading profile...</div>;
  if (error) return <div style={{ padding: '40px', color: 'red' }}>{error}</div>;

  return (
    <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
      <h2>My Profile</h2>

      <h3>Employment Details</h3>
      <table cellPadding="6">
        <tbody>
          <tr><td><strong>Employee Code</strong></td><td>{profile.employeeCode}</td></tr>
          <tr><td><strong>Name</strong></td><td>{profile.firstName} {profile.lastName}</td></tr>
          <tr><td><strong>Email</strong></td><td>{profile.email}</td></tr>
          <tr><td><strong>Department</strong></td><td>{profile.department?.name || '—'}</td></tr>
          <tr><td><strong>Designation</strong></td><td>{profile.designation || '—'}</td></tr>
          <tr><td><strong>Joining Date</strong></td><td>{profile.joiningDate}</td></tr>
          <tr><td><strong>Status</strong></td><td>{profile.status}</td></tr>
        </tbody>
      </table>

      <h3 style={{ marginTop: '24px' }}>Edit Personal Information</h3>
      <form onSubmit={handleSave} style={{ maxWidth: '400px' }}>
        <div style={{ marginBottom: '10px' }}>
          <label>Phone</label><br />
          <input
            type="text"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            style={{ width: '100%', padding: '6px' }}
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Address</label><br />
          <input
            type="text"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            style={{ width: '100%', padding: '6px' }}
          />
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Gender</label><br />
          <select value={gender} onChange={(e) => setGender(e.target.value)}>
            <option value="">Select</option>
            <option value="MALE">MALE</option>
            <option value="FEMALE">FEMALE</option>
            <option value="OTHER">OTHER</option>
          </select>
        </div>

        <div style={{ marginBottom: '10px' }}>
          <label>Date of Birth</label><br />
          <input
            type="date"
            value={dateOfBirth}
            onChange={(e) => setDateOfBirth(e.target.value)}
          />
        </div>

        {formMessage && <p style={{ color: 'green' }}>{formMessage}</p>}
        {formError && <p style={{ color: 'red' }}>{formError}</p>}

        <button type="submit" disabled={saving}>
          {saving ? 'Saving...' : 'Save Changes'}
        </button>
      </form>
    </div>
  );
};

export default ProfilePage;