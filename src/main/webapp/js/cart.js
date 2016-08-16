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
                cart.total_after_discount = 0.0;
                cart.state = 1;
                
				this.populateCart = function() {
					$http({
						method : 'GET',
						url : "https://vast-everglades-25484.herokuapp.com/rest/cart",
						headers : {
							'Authorization' : 'Bearer o1pjjkuo8vhmha5bip1898top1'
						}
					}).then(function successCallBack(response){
						cart.items = response.data['Items'];
						cart.total = response.data['Total price before discount'];
					    cart.discount = response.data['Total savings'];
					    cart.total_after_discount = response.data['Total price after discount'];
						console.log(cart.items);
						console.log(response.data);
					}, 
					function errorCallBack(){});
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