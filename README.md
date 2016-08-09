# Online shopping checkout workflow
Java and Jersey based REST API for emulating a shopping cart checkout workflow

API Guide
---------

- USER - Path : /users
  - **POST** Used to register a new user
    - Input : 
      - Format : ***x-www-form-urlencoded*** 
      - Parameters 
        - username(String) 
        - password(String)
        - firstname(String)
        - lastname(String)
        - email (String)
    - Output : 
      - Format : ***MediaType.APPLICATION_JSON*** 
      - Payload : JSON string containing the Response type, Response code and Message
    - Example :
        **POST /users , form parameters : username = vikcher123 , password = *hidden* , firstname = Vikas, lastname = Cheruku , email = vikcher123@gmail.com**
    - Response codes returned :
      - **701** : Username already exists
      - **800** : Internal server error occured
      - **600** : Successfully added user
      
  - **DELETE** Used to delete a user account 
    - ***Secured API. Requires the user to be logged in. The authorization token should be included in the HTTP authorization header***
    - Input : *None* . The username is obtained from the user auth token.
    - Output:
      - Format : ***MediaType.APPLICATION_JSON***
      - Payload : JSON string containing the Response type, Response code and Message
      - Example :
        **DELETE /users**
      - Response codes returned :
        - **702** - Username does not exist (Fatal error)
        - **800** - An internal server error occured
        - **600** - Successfully removed user account

- SESSION (Path : /sessions)
  - **POST** Used for user login
    - Input : 
      - Format : ***x-www-form-urlencoded*** 
      - Parameters 
        - username(String) 
        - password(String)
    - Output : 
      - Format : ***MediaType.APPLICATION_JSON*** 
      - Payload : JSON string containing the Response type, Response code and Message
      - Header : Authorization token
    - Example :
        **POST /sessions , form parameters : username = vikcher123 , password = *hidden**
    - Response codes returned :
      - **702** : Username already exists
      - **800** : Internal server error occured
      - **600** : User logged in successfully
      
  - **DELETE** Used for user logout
    - ***Secured API. Requires the user to be logged in. The authorization token should be included in the HTTP authorization header***
    - Input : No input. Username is obtained from Auth token
    - Output : 
      - Format : ***MediaType.APPLICATION_JSON*** 
      - Payload : JSON string containing the Response type, Response code and Message
    - Example :
        **DELETE /sessions , the currently logged in user will be logged out
    - Response codes returned :
      - **700** : User not logged in
      - **800** : Internal server error occured
      - **600** : User logged out successfully

- PRODUCT - Path : /products
  - **GET** Get a list of all products
    - - ***Unauthorzied API. Does not require the user to be logged in***
    - Input : Optional - Query param *category_id*
    - Output : 
      - Format : ***MediaType.APPLICATION_JSON*** 
      - Payload : JSON string containing the Response type, Response code, product count and product list
      - Each product JSON object in the JSON list contains:
        - Product ID
        - Product category
        - Product name
        - Product description
        - Price
        - Options (colors and sizes in JSON format)
        - Image URL
        - Discount on product
    - Example :
        - **GET /products?category_id=1**
        - Output :
        ```json
          {
            "Type": "Success",
            "Number of products": 2,
            "Product list": [
              {
                 "File URL": "/img/expressShorts.png",
                  "Product ID": 19,
                  "Category": "Men's shorts",
                  "Discount": 0,
                  "Product name": "Express - mens beach shorts",
                  "Price": 29.99,
                  "Product description": "Beach shorts for men - designed for comfort",
                  "options": "{ colors:['black','blue','brown'], sizes : [\"36\",\"38\",\"40\",\"42\",\"44\",\"46\"]}"
              },
              {
                "File URL": "/img/hollisterShorts.png",
                "Product ID": 20,
                "Category": "Men's shorts",
                "Discount": 0,
                "Product name": "Hollister shorts",
                "Price": 39.99,
                "Product description": "Trendy hollister shorts",
                "options": "{ colors:['black','blue','brown'], sizes : [\"36\",\"38\",\"40\",\"42\",\"44\",\"46\"]}"
              }
            ],
            "Response code": "600"
          }
          ```
    - Response codes returned :
      - **800** : Internal server error occured
      - **600** : Success
      
  
  - **GET /{productID} ** Get the details of product with specified ID
    - *** Does not require the user to be logged in***
    - Input : Path Param - product ID
    - Output : 
      - Format : ***MediaType.APPLICATION_JSON*** 
      - Payload : JSON string containing the Response type, Response code, product count(1) and product details
      - Each product JSON object in the JSON list contains:
        - Product ID
        - Product category
        - Product name
        - Product description
        - Price
        - Options (colors and sizes in JSON format)
        - Image URL
        - Discount on product
    - Example :
        - **GET /products/12**
        - Output :
        ```json
          {
              "Type": "Success",
              "Product details": {
                "File URL": "/img/levis501.png",
                "Product ID": 12,
                "Category": "Men's trousers",
                "Discount": 10,
                "Product name": "Levis Jeans - 501",
                "Price": 59.99,
                "Product description": "Signature Levis jeans style 501 (Straight fit)",
                "options": {
                  "sizes": [
                    "36",
                    "38",
                    "40",
                    "42",
                    "44",
                    "46"
                  ],
                  "colors": [
                    "black",
                    "blue",
                    "brown"
                  ]
                }
              }
            }
          ```
    - Response codes returned :
      - **800** : Internal server error occured
      - **600** : Success
      
- CATEGORY - Path : /categories
  - **GET** Get a list of all product categories
    - - ***Unauthorzied API. Does not require the user to be logged in***
    - Input : None required
    - Output : 
      - Format : ***MediaType.APPLICATION_JSON*** 
      - Payload : JSON string containing the Response type, Response code, category count and the category list
      - Each product JSON object in the JSON list contains:
        - Category ID
        - Category name
        - Category description
        - Discount on category (if applicable)
    - Example :
        - **GET /categories**
        - Output :
        ```json
          {
            "Type": "Success",
            "Category list": [
              {
                "Category_name": "Men's t-shirts",
                "Category discount": 5,
                "ID": 1,
                "Category_description": "T-shirts for men"
              },
              {
                "Category_name": "Men's shirts",
                "Category discount": 0,
                "ID": 2,
                "Category_description": "Formal shirts for men"
              },
              {
                "Category_name": "Men's shorts",
                "Category discount": 0,
                "ID": 4,
                "Category_description": "Shorts for men"
              },
              {
                "Category_name": "Men's footwear",
                "Category discount": 15,
                "ID": 5,
                "Category_description": "Footwear for men"
              },
              {
                "Category_name": "Men's trousers",
                "Category discount": 7,
                "ID": 3,
                "Category_description": "Jeans, trousers for men"
              }
            ],
            "Category Count": 5,
            "Response code": "600"
          }
          ```
    - Response codes returned :
      - **800** : Internal server error occured
      - **600** : Success
      
- CART - Path : /cart
  - **PUT** (/cart/{productID}?size='size'&color='color'&quantity='quantity'
    - Used to add a new item to cart. If the same item is present in the cart (same color and size), the quantity is just updated.
    - Input : 
      - Format : Query and path params
      - Parameters 
        - productID(int) - pathParam, required
        - size(String) - queryParam, required
        - color(String) - queryParam, required
        - quantity(String) - queryParam, required
    - Output : 
      - Format : ***MediaType.APPLICATION_JSON*** 
      - Payload : JSON string containing the Response type, Response code and Message
    - Example :
        **PUT /cart/1?productID=1&size=M&color=blue**
    - Response codes returned :
      - **702** : Color/size does not exist product / Product does not exist
      - **703** : Wrong format for parameter 
      - **704** : Missing input
      - **800** : Internal server error
      - **600** : Successfully added product to cart
  
  - **DELETE** (/cart/{productID}?size='size'&color='color'&quantity='quantity'
    - Used to delete an item from cart. If the same item is present in the cart (same color and size), the quantity is just updated. If the specified delete quantity is greater than quantity present in cart, error is raised.
    - Input : 
      - Format : Query and path params
      - Parameters 
        - productID(int) - pathParam, required
        - size(String) - queryParam, required
        - color(String) - queryParam, required
        - quantity(String) - queryParam, required
    - Output : 
      - Format : ***MediaType.APPLICATION_JSON*** 
      - Payload : JSON string containing the Response type, Response code and Message
    - Example :
        **DELETE /cart/1?productID=1&size=M&color=blue**
    - Response codes returned :
      - **702** : Color/size does not exist product / Product does not exist
      - **703** : Given quantity is greater than available in cart (Invalid input)
      - **704** : Missing input
      - **800** : Internal server error
      - **600** : Successfully removed specified quantity of product from cart
      - **601** : Nothing to remove from cart (Not considered a failure)
  
  - **DELETE** /cart
    - Used to empty the cart.
    - Input : None required
    - Output : 
      - Format : ***MediaType.APPLICATION_JSON*** 
      - Payload : JSON string containing the Response type, Response code and Message
    - Example :
        **DELETE /cart**
    - Response codes returned :
      - **800** : Internal server error
      - **600** : Successfully removed specified quantity of product from cart
  

  - **GET** /cart
    - Used to view cart of the logged in user.
    - Input : None required
    - Output :
      - Format : ***MediaType.APPLICATION_JSON***
      - Payload : Composite JSON object string containing the Response type, Response code, number of times, item list, price before discount, total savings, total price after discount
      - Each item list product in turn contains :
        - Product ID
        - Product name
        - Product description
        - Product price
        - Product price per qty
        - Quantity
        - Color
        - Image URL
        - Discount
      - Example :
        - **GET /cart**
        - Output :
        ```json
          {
            "Total price before discount": 194.95,
            "Total price after discount": 167.958,
            "Type": "Success",
            "Total number of items": 5,
            "Items": [
              {
                "Product ID": 1,
                "Discount": 14.994,
                "Product name": "Graphic Tee - TH",
                "Product price per qty": 24.99,
                "Product description": "Graphic Tee with a Tommy Hilfigher logo",
                "Color": "yellow",
                "Product price": 74.97,
                "Image URL": "/img/graphic_th.jpg",
                "Quantity": 3
              },
              {
                "Product ID": 12,
                "Discount": 11.998000000000001,
                "Product name": "Levis Jeans - 501",
                "Product price per qty": 59.99,
                "Product description": "Signature Levis jeans style 501 (Straight fit)",
                "Color": "black",
                "Product price": 119.98,
                "Image URL": "/img/levis501.png",
                "Quantity": 2
              }
            ],
            "Total savings": 26.992
          }
        ```
      - Response codes returned :
        - **800** : Internal server error
        - **600** : Successfully viewed cart
        
- **POST** /cart
    - Used for user checkout. The cart items are removed and receipt is displayed to the user.
    - Input : 
      - Format : ***x-www-form-urlencoded*** 
      - Parameters 
        - shipping_addr(String) - required
        - state(String) - required
        - promo_code(String) - optional
    - Output :
      - Format : ***MediaType.APPLICATION_JSON***
      - Payload : Composite JSON object string containing the Response type, Response code, number of items, item list, price before discount, price after product/category discount, discount percentage, promo code discount, price after promo code discount, promo code discount percentage, sales tax, price after tax.
      - Each item list product in turn contains :
        - Product ID
        - Product name
        - Product description
        - Product price
        - Product price per qty
        - Quantity
        - Color
        - Image URL
        - Discount
      - Example :
        - **POST /cart form parameters : username =  , password = *hidden* , firstname = Vikas, lastname = Cheruku , email = vikcher123@gmail.com**
        - Output :
        ```json
          {
              "Total price after discount": 839.916,
              "Tax state": "NC",
              "Promo code discount": 209.979,
              "Shipping address": "4206 Parable Way",
              "Total price before discount": 1634.8500000000001,
              "Type": "Success",
              "Total number of items": 15,
              "Sales tax percentage": "4.75%",
              "Sales tax amount": 29.9220075,
              "Items": [
                {
                  "Product ID": 18,
                  "Discount": 389.97,
                  "Product name": "Van Heusen - trousers",
                  "Product price per qty": 129.99,
                  "Product description": "Van Heusen signature formal trousers",
                  "Color": "brown",
                  "Product price": 779.94,
                  "Image URL": "/img/VHtrousers.png",
                  "Quantity": 6
                },
                {
                  "Product ID": 18,
                  "Discount": 389.97,
                  "Product name": "Van Heusen - trousers",
                  "Product price per qty": 129.99,
                  "Product description": "Van Heusen signature formal trousers",
                  "Color": "brown",
                  "Product price": 779.94,
                  "Image URL": "/img/VHtrousers.png",
                  "Quantity": 6
                },
                {
                  "Product ID": 1,
                  "Discount": 14.994,
                  "Product name": "Graphic Tee - TH",
                  "Product price per qty": 24.99,
                  "Product description": "Graphic Tee with a Tommy Hilfigher logo",
                  "Color": "red",
                  "Product price": 74.97,
                  "Image URL": "/img/graphic_th.jpg",
                  "Quantity": 3
                }
              ],
              "Promo code discount percentage": "25.0%",
              "Total savings": 794.9340000000001,
              "Total after promo code discount": 629.937,
              "Total after sales tax": 659.8590075
            }
        ```
      - Response codes returned :
        - **800** : Internal server error
        - **600** : Successfully checked out
        - **601** : No items in cart to checkout
        - **704** : Shipping address is required
        - **704** : Shipping state is required
        - **703** : State code is invalid
        - **703** : Promo code is invalid
        
