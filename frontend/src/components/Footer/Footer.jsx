import React from 'react'
import './Footer.css'
import { assets } from '../../assets/assets'

const Footer = () => {
  return (
    <div className='footer' id='footer'>
       <div className="footer-content">
         <div className="footer-content-left">
            <img src={assets.logo} alt="" />
            <p>Fresh, hot meals from your favourite kitchens, delivered fast to your door. Browse the menu, pay securely online and track every order until it reaches you.</p>
            <div className="footer-social-icons">
                <img src={assets.facebook_icon} alt="" />
                <img src={assets.twitter_icon} alt="" />
                <a href="https://www.linkedin.com/in/mihir-katyal-8a4107228/" target="_blank" rel="noopener noreferrer">
                  <img src={assets.linkedin_icon} alt="LinkedIn" />
                </a>
            </div>
         </div>
         <div className="footer-content-center">
                  <h2>COMPANY</h2>
                  <ul>
                    <li>Home</li>
                    <li>About Us</li>
                    <li>Delivery</li>
                    <li>Privacy Policiy</li>
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
       <p className="footer-copyright">Copyright 2024 © Tomato.com. All rights reserved.</p>
    </div>
  )
}

export default Footer
