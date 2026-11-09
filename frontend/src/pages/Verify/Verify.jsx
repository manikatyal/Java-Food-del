import React, { useContext, useEffect } from 'react';
import './Verify.css';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { StoreContext } from '../../context/StoreContext';
import axios from 'axios';

const Verify = () => {
  const [searchParams] = useSearchParams(); // Retrieves the query parameters from the URL
  const success = searchParams.get("success");
  const orderId = searchParams.get("orderId");

  const { url } = useContext(StoreContext); // Retrieve the base URL from context
  const navigate = useNavigate(); // Navigate to different routes

  const verifyPayment = async () => {
   console.log(success,orderId);
    try {
      const response = await axios.post(`${url}/api/order/verify`, { success, orderId });
      if (response.data.success) {
        navigate("/myorders");
      } else {
        navigate("/");
      }
    } catch (error) {
      console.error('Error verifying payment:', error);
    } 
   
  };

  useEffect(() => {
    verifyPayment();
  }, []); // Add dependencies to the useEffect hook

  return (
    <div className='verify'>
      <div className="spinner">
        {/* Spinner or loading indicator */}
      </div>
    </div>
  );
}

export default Verify;
