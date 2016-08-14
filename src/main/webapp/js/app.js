/**
 * 
 */

(function() {
	var app = angular.module('shoppingcart', []);
	
	/*
	app.controller('productController', ['$http', function($http) {
		var store = this;
		store.products = [];
		
		$http({method : 'GET', url : "https://vast-everglades-25484.herokuapp.com/rest/products"}).then(function successCallback(response){
			store.products = response.data.products;
			console.log(response.data.products);
		}, function errorCallBack(response) {
			
		});
	}]);
	*/
	
	app.controller('navController', function() {
		this.tab = 1;
		
		this.selectTab = function(setTab) {
			this.tab = setTab;
		};
		
		this.isSelected = function(checkTab) {
			return this.tab === checkTab;
		};
	});
	
	app.directive('productItem', function() {
		return {
			restrict: 'E',
		    templateUrl : "product-item.html",
		    controller : ['$http', function($http) {
		    	var store = this;
				store.products = [];
				
				$http({method : 'GET', url : "https://vast-everglades-25484.herokuapp.com/rest/products"}).then(function successCallback(response){
					store.products = response.data.products;
					console.log(response.data.products);
				}, function errorCallBack(response) {
					
				});
				
				this.addToCart = function(id, size, color, quantity){
					$http({
						method : 'PUT',
						url : "https://vast-everglades-25484.herokuapp.com/rest/cart/"+id+"?color=" + encodeURIComponent(color)
						       + "&size=" + encodeURIComponent(size) + "&quantity=" +encodeURIComponent(quantity),
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded',
							'Authorization' : 'Bearer o1pjjkuo8vhmha5bip1898top1'
						}
					}).then(function successCallBack(response){console.log(response)}, function errorCallBack(){});
				};
		    }],
		    controllerAs : 'store'
		};
	});
})();