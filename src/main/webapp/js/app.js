/**
 * 
 */

(function() {
	var app = angular.module('shoppingcart', ['navigation','products']);
	
	app.directive('cartItem', function() {
		return {
			restrict : 'E',
			templateUrl : "cart-item.html",
		}
	});
})();