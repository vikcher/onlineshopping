# Online shopping checkout workflow
Java and Jersey based REST API for emulating a shopping cart checkout workflow

API documentation
-----------------

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
