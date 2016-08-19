/**
 * The controller responsible for the shopping cart
 */

(function(){
	var app = angular.module('cart', []);
	
	/*
	 * Custom directive to render the cart and its different pages.
	 */
	app.directive('cartItem', function() {
		return {
			restrict : 'E',
			templateUrl : "cart-item.html",
			controller : ['$http', function($http) {
				var cart = this;
				cart.items = []; //List of items
                cart.total = 0.0;
                cart.discount = 0.0;
                cart.num_items = 0;
                cart.total_after_discount = 0.0;
                cart.state = 1; //The state of the checkout process 1- View cart 2 - Enter details 3 - Review cart 4 - Order confirmation
                cart.shipping_address = "";
                cart.shipping_state = "";
                cart.promo_code = "";
                cart.tax_percentage = 0.0;
                cart.sales_tax = 0.0;
                cart.total_num_items = 0;
                cart.promo_code_discount_percentage = 0.0;
                cart.promo_code_discount = 0.0;
                cart.total_after_promo_code_discount = 0.0;
                cart.grand_total = 0.0;
                cart.states = ["AK","AL","AR","AZ","CA","CO","CT","DC","DE","FL","GA","GU","HI","IA","ID", "IL","IN","KS","KY","LA","MA","MD","ME","MH","MI","MN","MO","MS","MT","NC","ND","NE","NH","NJ","NM","NV","NY", "OH","OK","OR","PA","PR","PW","RI","SC","SD","TN","TX","UT","VA","VI","VT","WA","WI","WV","WY"];
                cart.shipping_state = "";
                cart.confirmation_number = "";
                cart.errorMessage = "";
                cart.promo_code_valid = 0;
                
                /* Function sends a REST request to get cart items for the user */
				this.populateCart = function() {
					$http({
						method : 'GET',
						url : "https://vast-everglades-25484.herokuapp.com/rest/cart",
						headers : {
							'Authorization' : 'Bearer o1pjjkuo8vhmha5bip1898top1'
						}
					}).then(function successCallBack(response){
						if (response.data['Type'] == "Error") {
							cart.errorMessage = response.data['Message'];
						} else {
							cart.items = response.data['Items'];
							cart.total = response.data['Total price before discount'];
						    cart.discount = response.data['Total savings'];
						    cart.total_after_discount = response.data['Total price after discount'];
							console.log(cart.items);
							console.log(response.data);
						}
					}, 
					function errorCallBack(){});
				};
				
				/* Function to calculate the final amount including taxes after user enters shipping details */
				this.calculateFinalAmount = function() {
					$http({
						method : 'POST',
						url : "https://vast-everglades-25484.herokuapp.com/rest/cart",
						headers : {
							'Authorization' : 'Bearer o1pjjkuo8vhmha5bip1898top1',
							'Content-Type' : 'application/x-www-form-urlencoded'
						}, 
						data : 'shipping_addr='+encodeURIComponent(cart.shipping_address)+'&state='+encodeURIComponent(cart.shipping_state)+'&promo_code='+cart.promo_code
					}).then(function successCallBack(response){
						if (response.data['Type'] == "Error") {
							cart.errorMessage = response.data['Message'];
						} else {
							cart.items = response.data['Items'];
							cart.num_items = response.data['Total number of items'];
							cart.total = response.data['Total price before discount'];
						    cart.discount = response.data['Total savings'];
						    cart.total_after_discount = response.data['Total price after discount'];
						    cart.promo_code_discount_percentage = response.data['Promo code discount percentage'];
						    cart.promo_code_discount = response.data['Promo code discount'];
						    cart.total_after_promo_code_discount = response.data['Total after promo code discount'];
						    cart.tax_percentage = response.data['Sales tax percentage'];
						    cart.sales_tax = response.data['Sales tax amount'];
						    cart.grand_total = response.data['Total after sales tax'];
							console.log(cart);
						}
						console.log(response.data);
					}, 
					function errorCallBack(){});
					
				}
				
				/* Process order, return a confirmation number*/
				this.processOrder = function () {
					$http({
						method : 'DELETE',
						url : "https://vast-everglades-25484.herokuapp.com/rest/cart",
						headers : {
							'Authorization' : 'Bearer o1pjjkuo8vhmha5bip1898top1'
						}
					}).then(function successCallBack(response){
						cart.confirmation_number = response.data.confirmation;
						cart.total = 0.0;
						cart.discount = 0.0;
						cart.num_items = 0;
						cart.total_after_discount = 0.0;
						cart.shipping_address = "";
						cart.shipping_state = "";
						cart.promo_code = "";
						cart.tax_percentage = 0.0;
						cart.sales_tax = 0.0;
						cart.total_num_items = 0;
						cart.promo_code_discount_percentage = 0.0;
						cart.promo_code_discount = 0.0;
						cart.total_after_promo_code_discount = 0.0;
						cart.grand_total = 0.0;
						cart.shipping_state = "";
					}, 
					function errorCallBack(){});
					
				};
				
				/* Delete a specific item from the cart */
				this.deleteItem = function(color,size,quantity, pid) {
					$http({
						method : 'DELETE',
						url : "https://vast-everglades-25484.herokuapp.com/rest/cart/"+encodeURIComponent(pid)+"?color="+encodeURIComponent(color)+"&size="+encodeURIComponent(size)+"&quantity="+encodeURIComponent(quantity),
						headers : {
							'Authorization' : 'Bearer o1pjjkuo8vhmha5bip1898top1'
						}
					}).then(function successCallBack(response){cart.populateCart();}, function errorCallBack(){});
				};
				
				this.getValidity = function () {
					return cart.success == 1;
				};
				
				this.setState = function(state) {
					this.state = state;
				};
				
				this.getState = function(state) {
					return this.state;
				}
			}],
			controllerAs : 'cart'
		}
	});
})();