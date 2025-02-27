import { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
    const { isAuthenticated, user, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    if (!isAuthenticated) {
        return <p>You are not logged in</p>;
    }

    return (
        <div className="dashboard-container">
            <h2>Welcome to UnCypher</h2>

            {/* ✅ Display username above the logout button */}
            {user?.email && <p className="username">Logged in as: {user.email}</p>}

            <button className="logout-button" onClick={handleLogout}>Logout</button>
        </div>
    );
};

export default Dashboard;
