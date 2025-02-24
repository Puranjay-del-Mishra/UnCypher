import { useState } from 'react'
import AuthPage from './pages/AuthPage.jsx'
import Dashboard  from './pages/Dashboard.jsx'
import './App.css'
import { Link, BrowserRouter as  Router, Routes, Route } from 'react-router-dom';
import logo from './assets/uncypher_logo.png'
import './styles/Navbar.css';

function App() {
  const [count, setCount] = useState(0)

  return (
    <Router>
      <Navbar />
      <Routes>
          <Route path="/" element = {<AuthPage />}/>
          <Route path="/dashboard" element= {<Dashboard />}/>
      </Routes>
    </Router>
  );
}

function Navbar(){
return (
       <nav className='nav'>
        <Link to="/" className="nav-logo">
            <img src={logo} alt="UnCypher Logo" className="logo" />
            <span className="brand-name">UnCypher</span>
        </Link>
       </nav>
    );
}

export {App, Navbar};
