import React, { useContext, useEffect } from 'react';
import './FoodDisplay.css';
import { StoreContext } from '../../context/StoreContext';
import FoodItem from '../FoodItem/FoodItem';
import axios from 'axios';

const FoodDisplay = ({ category }) => {
  const { food_list, setFoodList, searchText, url } = useContext(StoreContext);

  // GET SEARCH ITEMS FROM BACKEND
  const searchFood = async () => {
    try {
      const response = await axios.post(`${url}/api/food/search`, { name: searchText });
      if (response.data.success) {
        setFoodList(response.data.data); // Update the food list with the search results
      }
    } catch (error) {
      console.error("Error fetching food:", error);
    }
  };

  useEffect(() => {
    if (searchText !== "") {
      searchFood(); // Trigger the search when searchText changes
    } else {
      fetchFoodList(); // Fetch full list if searchText is empty
    }
  }, [searchText]);

  const fetchFoodList = async () => {
    try {
      const response = await axios.get(`${url}/api/food/list`);
      setFoodList(response.data.data);
    } catch (error) {
      console.error("Error fetching food list:", error);
    }
  };

  return (
    <div className='food-display' id='food-display'>
      <h2>Top dishes near you</h2>
      <div className='food-display-list'>
        {food_list.map((item, index) => {
          if (category === 'All' || category === item.category) {
            return (
              <FoodItem
                key={index}
                id={item._id}
                name={item.name}
                description={item.description}
                image={item.image}
                price={item.price}
              />
            );
          }
          return null; // Avoid rendering anything if the category doesn't match
        })}
      </div>
    </div>
  );
};

export default FoodDisplay;
