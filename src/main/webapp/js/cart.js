/**
 * 
 */

(function(){
	var app = angular.module('cart', []);
	
	app.directive('cartItem', function() {
		return {
			restrict : 'E',
			templateUrl : "cart-item.html",
			controller : ['$http', function($http) {
				var cart = this;
				cart.items = [];
                cart.total = 0.0;
                cart.discount = 0.0;
                cart.num_items = 0;
                cart.total_after_discount = 0.0;
                cart.state = 1;
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
                cart.success = 0;
                cart.errorMessage = "";
                cart.promo_code_valid = 0;
                
				this.populateCart = function() {
					$http({
						method : 'GET',
						url : "https://vast-everglades-25484.herokuapp.com/rest/cart",
						headers : {
							'Authorization' : 'Bearer o1pjjkuo8vhmha5bip1898top1'
						}
					}).then(function successCallBack(response){
						if (response.data['Type'] == "Error") {
							cart.success = 0;
							cart.errorMessage = response.data['Message'];
						} else {
							cart.items = response.data['Items'];
							cart.total = response.data['Total price before discount'];
						    cart.discount = response.data['Total savings'];
						    cart.total_after_discount = response.data['Total price after discount'];
						    cart.success = 1;
							console.log(cart.items);
							console.log(response.data);
						}
					}, 
					function errorCallBack(){});
				};
				
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
							cart.success = 0;
							cart.errorMessage = response.data['Message'];
							//console.log(response.data);
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
						    cart.success = 1;
							console.log(cart);
						}
						console.log(response.data);
					}, 
					function errorCallBack(){});
					
				}
				
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
		                cart.confirmation_number = "";
					}, 
					function errorCallBack(){});
					
				};
				
				this.resetValidity = function() {
					cart.success = 0;
				};
				
				this.getValidity = function () {
					return cart.success == 1;
				};
				
				this.setState = function(state) {
					this.state = state;
					
				};
				
				this.goToNextStateIfValid = function(state) {
					console.log ("Cart success is " + cart.success);
					if (cart.success == 1) {
						this.state = state;
					}
				}
				
				this.getState = function(state) {
					return this.state;
				}
			}],
			controllerAs : 'cart'
		}
	});
})();