import { useState, useEffect } from 'react';
import { getAllEmployees } from '../services/employeeService';

const STATUS_OPTIONS = ['ACTIVE', 'INACTIVE', 'ON_NOTICE', 'RESIGNED', 'TERMINATED'];

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
    setPage(0);
    loadEmployees();
  };

  return (
    <div style={{ padding: '40px', fontFamily: 'sans-serif' }}>
      <h2>Employees</h2>

      <form onSubmit={handleSearch} style={{ marginBottom: '20px', display: 'flex', gap: '10px' }}>
        <input
          type="text"
          placeholder="Search name, email, code..."
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
        <input
          type="text"
          placeholder="Department"
          value={department}
          onChange={(e) => setDepartment(e.target.value)}
        />
        <select value={status} onChange={(e) => setStatus(e.target.value)}>
          <option value="">All Statuses</option>
          {STATUS_OPTIONS.map((s) => (
            <option key={s} value={s}>{s}</option>
          ))}
        </select>
        <button type="submit">Search</button>
      </form>

      {error && <p style={{ color: 'red' }}>{error}</p>}
      {loading && <p>Loading employees...</p>}

      {!loading && employees.length === 0 && <p>No employees found.</p>}

      {!loading && employees.length > 0 && (
        <>
          <table border="1" cellPadding="8" style={{ borderCollapse: 'collapse' }}>
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
                  <td>{emp.status}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div style={{ marginTop: '12px' }}>
            <button
              onClick={() => setPage((p) => Math.max(p - 1, 0))}
              disabled={page === 0}
            >
              Previous
            </button>
            <span style={{ margin: '0 10px' }}>
              Page {page + 1} of {totalPages}
            </span>
            <button
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