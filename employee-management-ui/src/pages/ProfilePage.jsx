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

  if (loading) {
    return <div className="loading-state">Loading profile...</div>;
  }

  if (error) {
    return <div className="alert alert-error">{error}</div>;
  }

  return (
    <div>
      <div className="page-header">
        <h1>My Profile</h1>
        <p>View your employment details and update personal information</p>
      </div>

      {/* Employment Details */}
      <div className="card mb-6">
        <div className="card-header">
          <h3 className="card-title">Employment Details</h3>
        </div>

        <div className="table-container">
          <table className="table">
            <tbody>
              <tr>
                <td style={{ width: '180px', fontWeight: 500 }}>Employee Code</td>
                <td>{profile.employeeCode}</td>
              </tr>
              <tr>
                <td style={{ fontWeight: 500 }}>Name</td>
                <td>{profile.firstName} {profile.lastName}</td>
              </tr>
              <tr>
                <td style={{ fontWeight: 500 }}>Email</td>
                <td>{profile.email}</td>
              </tr>
              <tr>
                <td style={{ fontWeight: 500 }}>Department</td>
                <td>{profile.department?.name || '—'}</td>
              </tr>
              <tr>
                <td style={{ fontWeight: 500 }}>Designation</td>
                <td>{profile.designation || '—'}</td>
              </tr>
              <tr>
                <td style={{ fontWeight: 500 }}>Joining Date</td>
                <td>{profile.joiningDate}</td>
              </tr>
              <tr>
                <td style={{ fontWeight: 500 }}>Status</td>
                <td>{profile.status}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      {/* Edit Personal Information */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">Edit Personal Information</h3>
        </div>

        <form onSubmit={handleSave} style={{ maxWidth: '420px' }}>
          <div className="form-group">
            <label className="form-label">Phone</label>
            <input
              type="text"
              className="form-input"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              placeholder="Phone number"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Address</label>
            <input
              type="text"
              className="form-input"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
              placeholder="Address"
            />
          </div>

          <div className="form-group">
            <label className="form-label">Gender</label>
            <select
              className="form-select"
              value={gender}
              onChange={(e) => setGender(e.target.value)}
            >
              <option value="">Select</option>
              <option value="MALE">MALE</option>
              <option value="FEMALE">FEMALE</option>
              <option value="OTHER">OTHER</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Date of Birth</label>
            <input
              type="date"
              className="form-input"
              value={dateOfBirth}
              onChange={(e) => setDateOfBirth(e.target.value)}
            />
          </div>

          {formMessage && <div className="alert alert-success">{formMessage}</div>}
          {formError && <div className="alert alert-error">{formError}</div>}

          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving...' : 'Save Changes'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default ProfilePage;