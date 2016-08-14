/**
 * 
 */

(function() {
	var app = angular.module('shoppingcart', []);
	
	app.controller('storeController', ['$http', function($http) {
		var store = this;
		
		store.products = [];
		
		$http.get({method : 'GET', url : 'http://vast-everglades-25484.herokuapp.com/rest/products'}).then(function successCallback(response){
			store.products = response.products;
		}, function errorCallBack(response) {
			
		});
	}]);
})();