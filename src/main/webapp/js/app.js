/**
 * 
 */

(function() {
	var app = angular.module('shoppingcart', []);
	
	app.controller('productController', ['$http', function($http) {
		var store = this;
		store.products = [];
		
		$http({method : 'GET', url : "https://vast-everglades-25484.herokuapp.com/rest/products"}).then(function successCallback(response){
			store.products = response.data.products;
			console.log(response.data.products);
		}, function errorCallBack(response) {
			
		});
	}]);
	
	app.controller('navController', function() {
		this.tab = 1;
		
		this.selectTab = function(setTab) {
			this.tab = tab;
		};
		
		this.isSelected = function(checkTab) {
			return this.tab === checkTab;
		};
	});
})();