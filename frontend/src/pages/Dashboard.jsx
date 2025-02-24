import { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
    const {isAuthenticated, user, logout} = useContext(AuthContext);

    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    if(!isAuthenticated){
        return <p>You are not logged in</p>;
    }

    return (
        <div>
        <h2>Welcome, {user?.email}!</h2>
        <button onClick={handleLogout}>Logout</button>
        </div>
    );
};

export default Dashboard;
