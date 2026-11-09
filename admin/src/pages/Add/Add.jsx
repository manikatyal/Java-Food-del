import React, { useEffect, useState } from 'react' // Importing React library
import "./Add.css" // Importing CSS file for styling
import { assets } from '../../assets/assets' // Importing assets
import axios from 'axios';
import { toast } from 'react-toastify';

const Add = ({url}) => {

  
   const [image,setImage]=useState(false);
   const [data,setData]=useState({
    name:"",
    description:"",
    price:"",
    category:"Salad"
   })

   const onChangeHandler=(event)=>{  {/* THIS WILL BE CALLED FOR EVERY FIELD JUST BY CHANGING THE VALUE OF FIELD TO NAME,DES,PRICE,CATEGORY,WE WILL GET ITS VALUE THROUGH THIS FUNTION ONLY */}
      const name=event.target.name;
      const value=event.target.value;
      setData(data=>({...data,[name]:value}))
   }

   const onSubmitHandler=async (event) =>{
         event.preventDefault();{/*This will make sure ki onclickng add btn the page donot reload */}
         const formData=new FormData();
         formData.append("name",data.name);
         formData.append("description",data.description);
         formData.append("price",Number(data.price));
         formData.append("category",data.category);
         formData.append("image",image);
         const response= await axios.post(`${url}/api/food/add`,formData);
         if(response.data.success){
            setData({
                name:"",
                description:"",
                price:"",
                category:"Salad"
               });
               setImage(false);
               toast.success(response.data.message); {/*Add notification in our wb whenver we add food correctly */}
         }else{
                toast.error(response.data.message)
         }
   }

  return (
    <div className='add'> {/* Main container with class 'add' */}
        <form className="flex-col" onSubmit={onSubmitHandler}> {/* Form with a class 'flex-col' for flexbox column layout */}
            <div className="add-img-upload flex-col"> {/* Container for image upload section */}
                <p>Upload Image</p> {/* Label for the image upload */}
                <label htmlFor="image"> {/* Label for the file input */}
                    <img src={image?URL.createObjectURL(image):assets.upload_area} alt="" /> {/*  Image indicating upload area  URL VALA STUFF MAKES SURE KI WHEN WE HAVE SEL THE IMG THIS IMG IS SHOWN ON THE WB INSTEAD OF UPLOAD AREA IMG */}
                </label>
                <input onChange={(e)=>{setImage(e.target.files[0])}}  type="file" id='image' hidden required /> {/*logic to addimg file Hidden file input field */}
            </div>
            <div className="add-product-name flex-col"> {/* Container for product name input */}
                <p>Product name</p> {/* Label for product name input */}
                <input onChange={onChangeHandler} value={data.name}  type="text" name="name" placeholder='Type here' /> {/* Text input for product name */}
            </div>

            <div className="add-product-description flex-col"> {/* Container for product description */}
                <p>Product description</p> {/* Label for product description */}
                <textarea onChange={onChangeHandler} value={data.description}  name="description" rows="6" placeholder='Write content here' required></textarea> {/* Textarea for product description */}
            </div>
            <div className='add-category-price'> {/* Container for category and price */}
                <div className="add-category flex-col"> {/* Container for category selection */}
                    <p>Product category</p> {/* Label for product category */}
                    <select onChange={onChangeHandler} value={data.category}  name="category"> {/* Dropdown for selecting product category */}
                        <option value="Salad">Salad</option>
                        <option value="Rolls">Rolls</option>
                        <option value="Deserts">Deserts</option>
                        <option value="Sandwich">Sandwich</option>
                        <option value="Cake">Cake</option>
                        <option value="Pure Veg">Pure Veg</option>
                        <option value="Pasta">Pasta</option>
                        <option value="Noodles">Noodles</option>
                    </select>
                </div>
                <div className="add-price flex-col"> {/* Container for price input */}
                    <p>Product Price</p> {/* Label for product price */}
                    <input onChange={onChangeHandler} value={data.price}  type="Number" name='price' placeholder='$20' /> {/* Number input for product price */}
                </div>
            </div>
            <button type='submit' className='add-btn'>ADD</button> {/* Submit button with class 'add-btn' */}
        </form> 
    </div>
  )
}

export default Add // Exporting the Add component as default
