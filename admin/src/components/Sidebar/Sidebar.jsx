import React from 'react'
import './Sidebar.css' 
import { assets } from '../../assets/assets'
import { NavLink } from 'react-router-dom'

const Sidebar = () => {
  return (
    <div className='sidebar'>
     <div className="sidebar-options">
        <NavLink to='/add' className="sidebar-option">
            <img src={assets.add_icon} alt="" />
            <p>Add Items</p>
        </NavLink>
        <NavLink to='/list' className="sidebar-option">
            <img src={assets.order_icon} alt="" />
            <p>List Items</p>
        </NavLink>
        <NavLink to='/orders' className="sidebar-option"> {/*Due to navlink whenever we clcik on any content of sideicon to vha k page pe to jaaega hi plus when they r clicked ek active class bi add ho jaaegi jispe bhi click kia ho jo use hogi color krne k liye cliked valid option ko*/ }
            <img src={assets.order_icon} alt="" />
            <p>Orders</p>
        </NavLink>
     </div>
    </div>
  )
}

export default Sidebar
