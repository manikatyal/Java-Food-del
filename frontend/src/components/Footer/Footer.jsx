import React from 'react'
import { Link } from 'react-router-dom'
import './Footer.css'
import { assets } from '../../assets/assets'

const Footer = () => {
  return (
    <div className='footer' id='footer'>
       <div className="footer-content">
         <div className="footer-content-left">
            <img src={assets.logo} alt="Tomato" />
            <p>Fresh, hot meals from your favourite kitchens, delivered fast to your door. Browse the menu, pay securely online and track every order until it reaches you.</p>
            <div className="footer-social-icons">
                <a href="https://www.linkedin.com/in/mihir-katyal-8a4107228/" target="_blank" rel="noopener noreferrer" title="LinkedIn">
                  <img src={assets.linkedin_icon} alt="LinkedIn" />
                </a>
                <a href="https://github.com/manikatyal/Java-Food-del" target="_blank" rel="noopener noreferrer" title="Source code on GitHub" className="footer-github">
                  <svg viewBox="0 0 16 16" aria-hidden="true">
                    <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/>
                  </svg>
                </a>
            </div>
         </div>
         <div className="footer-content-center">
                  <h2>QUICK LINKS</h2>
                  <ul>
                    <li><Link to="/">Home</Link></li>
                    <li><a href="/#explore-menu">Menu</a></li>
                    <li><Link to="/cart">Cart</Link></li>
                    <li><Link to="/myorders">My Orders</Link></li>
                  </ul>
         </div>
         <div className="footer-content-right">
               <h2>GET IN TOUCH</h2>
             <ul>
                <li><a href="tel:+918700296322">+91 87002 96322</a></li>
                <li><a href="mailto:manikatyal6@gmail.com">manikatyal6@gmail.com</a></li>
             </ul>
         </div>

       </div>
       <hr />
       <p className="footer-copyright">© {new Date().getFullYear()} Mihir Katyal. Built with React, Spring Boot and MongoDB.</p>
    </div>
  )
}

export default Footer
