import { useState, useEffect } from 'react';
import { getAllEmployees } from '../services/employeeService';

const STATUS_OPTIONS = ['ACTIVE', 'INACTIVE', 'ON_NOTICE', 'RESIGNED', 'TERMINATED'];

/* Map status to badge style */
const getStatusBadge = (status) => {
  switch (status) {
    case 'ACTIVE':
      return 'badge badge-success';
    case 'ON_NOTICE':
      return 'badge badge-warning';
    case 'INACTIVE':
    case 'RESIGNED':
    case 'TERMINATED':
      return 'badge badge-neutral';
    default:
      return 'badge badge-neutral';
  }
};

const EmployeeListPage = () => {
  const [employees, setEmployees] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [keyword, setKeyword] = useState('');
  const [department, setDepartment] = useState('');
  const [status, setStatus] = useState('');

  const loadEmployees = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await getAllEmployees({
        keyword: keyword || undefined,
        department: department || undefined,
        status: status || undefined,
        page,
        size: 10,
      });
      setEmployees(data.content);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError('Failed to load employees');
    } finally {
      setLoading(false);
    }
  };

    useEffect(() => {
      loadEmployees();
    }, [page]);

    const handleSearch = (e) => {
      e.preventDefault();
      // Always reset to first page when searching
      if (page === 0) {
        // Already on page 0 → just reload with current filters
        loadEmployees();
      } else {
        // Changing page will trigger the useEffect above
        setPage(0);
      }
    };

  return (
    <div>
      <div className="page-header">
        <h1>Employees</h1>
        <p>Search and view employee records</p>
      </div>

      {/* Search / Filter card */}
      <div className="card mb-6">
        <form onSubmit={handleSearch}>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '12px', alignItems: 'flex-end' }}>
            <div className="form-group" style={{ marginBottom: 0, flex: '1 1 180px' }}>
              <label className="form-label">Search</label>
              <input
                type="text"
                className="form-input"
                placeholder="Name, email, code..."
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
              />
            </div>

            <div className="form-group" style={{ marginBottom: 0, flex: '1 1 140px' }}>
              <label className="form-label">Department</label>
              <input
                type="text"
                className="form-input"
                placeholder="Department"
                value={department}
                onChange={(e) => setDepartment(e.target.value)}
              />
            </div>

            <div className="form-group" style={{ marginBottom: 0, flex: '1 1 140px' }}>
              <label className="form-label">Status</label>
              <select
                className="form-select"
                value={status}
                onChange={(e) => setStatus(e.target.value)}
              >
                <option value="">All Statuses</option>
                {STATUS_OPTIONS.map((s) => (
                  <option key={s} value={s}>{s}</option>
                ))}
              </select>
            </div>

            <button type="submit" className="btn btn-primary">
              Search
            </button>
          </div>
        </form>
      </div>

      {error && <div className="alert alert-error">{error}</div>}

      {loading && <div className="loading-state">Loading employees...</div>}

      {!loading && employees.length === 0 && (
        <div className="card">
          <div className="empty-state">No employees found.</div>
        </div>
      )}

      {!loading && employees.length > 0 && (
        <>
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Code</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Department</th>
                  <th>Designation</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {employees.map((emp) => (
                  <tr key={emp.id}>
                    <td>{emp.employeeCode}</td>
                    <td>{emp.firstName} {emp.lastName}</td>
                    <td>{emp.email}</td>
                    <td>{emp.department?.name || '—'}</td>
                    <td>{emp.designation || '—'}</td>
                    <td>
                      <span className={getStatusBadge(emp.status)}>
                        {emp.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Pagination */}
          <div className="flex gap-3 mt-4" style={{ alignItems: 'center', justifyContent: 'flex-end' }}>
            <button
              className="btn btn-secondary btn-sm"
              onClick={() => setPage((p) => Math.max(p - 1, 0))}
              disabled={page === 0}
            >
              Previous
            </button>
            <span className="text-secondary" style={{ fontSize: '14px' }}>
              Page {page + 1} of {totalPages || 1}
            </span>
            <button
              className="btn btn-secondary btn-sm"
              onClick={() => setPage((p) => p + 1)}
              disabled={page + 1 >= totalPages}
            >
              Next
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default EmployeeListPage;