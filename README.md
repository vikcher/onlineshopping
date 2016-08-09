# Online Shopping REST API
Java and Jersey based REST API for emulating a shopping cart checkout workflow

Use cases
=========
- User registration *(See POST /user in API guide)*
- User de-registration *(SEE DELETE/user in API guide)*
- User login *(See POST /sessions in API guide)*
- User logout *(SEE DELETE /sessions in API guide)*
- View product categories *(See GET /categories in API guide)*
- View all products or products by category *(See GET /products in API guide)*
- View specific product by ID *(See GET /products/{productID} in API guide)*
- Add product to cart *(See PUT /cart/{productID} in API guide)*
- Remove product from cart *(See DELETE /cart/{productID} in API guide)*
- Empty cart *(See DELETE /cart in API guide)*
- View cart *(See GET /cart in API guide)*
- Apply promo codes *(See POST /cart in API guide)*
- Enter shipping and billing address *(See POST /cart in API guide)*
- Apply sales tax based on shipping state *(See POST /cart in API guide)*
- Apply discounts by product or product category
- Checkout returns the transaction details and receipt *(See POST /cart in API guide)*

Data model
==========
ER diagram for the data model
-----------------------------
![onlineshopping-erd](https://cloud.githubusercontent.com/assets/4031535/17532273/da39e3ec-5e4d-11e6-929b-76c00310c345.png)

Tables
------
- User : Contains the information about all the users.
  - User ID (primary key)
  - Username
  - Password
  - First name
  - Last name
  - Email ID
- Sessions : Contains the present user sessions information
  - User ID (Foreign key with the users table)
  - Session token
- Products : Contains the details of products.
  - Product ID (primary key)
  - Category ID (foreign key with the categories table)
  - Product name
  - Product description
  - Product price
  - Options (color and sizes)
- Product discounts : Contains the discounts applicable to products
  - Product ID (Foreign key with the Products table)
  - Discount
- Category discounts : Contains the discounts applicable to categories
  - Category ID (Foreign key with the Categories table)
  - Discount
- Cart : Associates the User ID with the corresponding Cart ID
  - User ID (Foreign key with the user table)
  - Cart ID (Primary key)
- Cart_products : Contains all the products in carts.
  - cart_product_id (Primary key)
  - cart_id (Foreign key with the cart table)
  - product_id (Foreign key with the Products table)
  - Color
  - Size
  - Quantity
- Promo_codes : Contains the promo codes and discounts
  - promo_code (Primary key)
  - Discount
- sales_tax : Contains the sales tax percentage for each state
  - state (Primary key)
  - sales_tax

API guide
==========
Response codes
---------------
  - All the API calls return a 200 OK HTTP response but the custom response codes are embedded in the JSON response. All response codes starting with (6xx) denote success messages. Response codes starting with 7 (7xx) denote errors with  the API input. Response codes starting with 8 (8xx) denote system errors.
  - All the APIs return response in JSON format
  - Success codes
    - 600 : Operation succeeded
    - 601 : Nothing to do (No-Op)
  - API failure codes
    - 700 : Not authorized
    - 701 : Already exists
    - 702 : Does not exist
    - 703 : Invalid input
    - 704 : Missing input
  - System error codes
    - 800 : Internal system error
- APIs needing authorization
  Some APIs need the user to be logged in. **When the user is created using POST /user (See API reference), the Response Header returns an Authentication token. This token should be added to the Request 'Authorization' header with the prefix 'Bearer'. For instance, if the token is '38sdagks7aoydyg2', the Authorization header should contain 'Bearer 38sdagks7aoydyg2'**. The list of APIs needing token-based authorization are:
    - DELETE /user
    - DELETE /sessions
    - GET /cart
    - PUT /cart
    - POST /cart

API Reference
==============

- USER Path : /users
  --------------------
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
    - **Requires user authentication token in request header**
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
  --------------------------
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
    - ** Requires user authentication token **
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
  --------------------------
  - **GET** Get a list of all products
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
        - Response :
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
        - Response :
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
  -----------------------------
  - **GET** Get a list of all product categories
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
        - Response :
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
  --------------------
  - **PUT** (/cart/{productID}?size='size'&color='color'&quantity='quantity'
    - ** Requires user Authorization token in the request header**  
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
    - ** Requires user Authorization token in the request header**
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
    - ** Requires user Authorization token in the request header**
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
    - ** Requires user Authorization token in the request header **
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
        - Response :
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
  - **Requires user Authentication token in request header** 
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
        - Response :
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
        
Retrospective and possible enhancements
=======================================
- The Cart relation in the database is not really required, the User ID can be used as a key for cart_product_id . It was included with an order history use case in mind which was not implemented.
- There can be another layer to the checkout process - by returning the order details to the user first and returning a confirmation. The present design is a simplified version where confirmation is not required.
- The native JDBC code does not allow for efficient refactoring as it does not allow proper closing of connections. Using a framework such as Spring JDBC framework would have made it better.
- Caching frequent queries such as user sessions, user cart contents in a persistent cache like Redis or Memcached will improve the performance and bandwidth usage greatly.
- The authorization tokens for users should have a timeout value. This can be possible if they are stored in Redis or Memcached. This is presently not implemented.
- Using Jackson library for Java for JSON parsing from Objects to Java can make requests and responses much easier.
- Adding a guest checkout feature
- Adding pagination for requests such as view products, so that only a certain number of items can be retrieved per request.   This optimizes bandwidth usage and performance

References
==========
- https://dzone.com/articles/storing-passwords-java-web
- http://stackoverflow.com/questions/26777083/best-practice-for-rest-token-based-authentication-with-jax-rs-and-jersey
- https://jersey.java.net/documentation/latest/index.html

Appendix
=========
Promo codes for testing
-----------------------
- 50OFF (50% off)
- GET25OFF (25% off)
- OLYMPICSGOUSA (10% off)
