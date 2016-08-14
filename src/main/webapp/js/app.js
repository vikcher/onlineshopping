/**
 * 
 */

(function() {
	var app = angular.module('shoppingcart', []);
	
	app.controller('storeController', ['$http', function($http) {
		//this.products = [{name: 'a', price: 200}, {name : 'b', price : 500}];
		var store = this;
		
		store.products = [];
		
		$http({method : 'GET', url : "https://vast-everglades-25484.herokuapp.com/rest/products"}).then(function successCallback(response){
			store.products = response.data.products;
			console.log(response.data.products);
		}, function errorCallBack(response) {
			
		});
	}]);
})();