import React, { useContext, useState } from 'react';
import './Navbar.css';
import { assets } from '../../assets/assets';
import { Link, useNavigate } from 'react-router-dom';
import { StoreContext } from '../../context/StoreContext';

const Navbar = ({ setShowLogin }) => {
  const [menu, setMenu] = useState('home');
  const { getTotalCartAmount, token, setToken, setSearchText } = useContext(StoreContext);
  const [inputValue, setInputValue] = useState('');
  const navigate = useNavigate();

  const logout = () => {
    localStorage.removeItem("token");
    setToken("");
    navigate("/"); // Navigate to the homepage on logout
  };

  const handleSearch = () => {
    setSearchText(inputValue); // Set searchText to trigger search
    navigate('/'); // Navigate to the page where the FoodDisplay component is located
  };

  return (
    <div className='navbar'>
      <Link to='/'><img src={assets.logo} alt="" className="logo" /></Link>
      <ul className='navbar-menu'>
        <Link to='/' onClick={() => { setMenu('home'); }} className={menu === 'home' ? "active" : ""}>home</Link>
        <a href='#explore-menu' onClick={() => { setMenu('menu'); }} className={menu === 'menu' ? "active" : ""}>menu</a>
        <a href='#app-download' onClick={() => { setMenu('mobile-app'); }} className={menu === 'mobile-app' ? "active" : ""}>mobile app</a>
        <a href='#footer' onClick={() => { setMenu('contact-us'); }} className={menu === 'contact-us' ? "active" : ""}>contact us</a>
      </ul>
      <div className='navbar-right'>
        <input
          type="text"
          placeholder="Search-Food"
          value={inputValue}
          onChange={(e) => setInputValue(e.target.value)} // Update state as user types
        />
        <img
          onClick={handleSearch} // Call handleSearch when the search icon is clicked
          src={assets.search_icon}
          alt="Search Icon"
        />
        <div className="navbar-search-icon">
          <Link to='/cart'><img src={assets.basket_icon} alt="" /></Link>
          <div className={getTotalCartAmount() === 0 ? "" : "dot"}></div>
        </div>
        {
          !token ?
            <button onClick={() => setShowLogin(true)}>sign-in</button> :
            <div className='navbar-profile'>
              <img src={assets.profile_icon} alt="" />
              <ul className="nav-profile-dropdown">
                <li onClick={() => navigate("/myorders")}><img src={assets.bag_icon} alt="" /><p>Orders</p></li>
                <hr />
                <li onClick={logout}><img src={assets.logout_icon} alt="" /><p>Logout</p></li>
              </ul>
            </div>
        }
      </div>
    </div>
  );
};

export default Navbar;
