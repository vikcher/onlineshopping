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
				
				$http({
					method : 'GET',
					url : "https://vast-everglades-25484.herokuapp.com/rest/cart",
					headers : {
						'Authorization' : 'Bearer o1pjjkuo8vhmha5bip1898top1'
					}
				}).then(function successCallBack(response){
					cart.items = response.data['Items'];
					console.log(cart.items);
					console.log(response.data);
				}, 
				function errorCallBack(){});
			}],
			controllerAs : 'cart'
		}
	});
})();