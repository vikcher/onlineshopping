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
				
				this.addToCart = function(size, color, quantity){
					$http({
						method : 'POST',
						url : "https://vast-everglades-25484.herokuapp.com/rest/cart",
						headers : {
							'Content-Type' : 'application/x-www-form-urlencoded',
							'Authorization' : 'Bearer o1pjjkuo8vhmha5bip1898top1'
						},
						data : 'size='+size+'&color='+color+'&quantity'+quantity
					}).then(function successCallBack(response){}, function errorCallBack(){});
				};
		    }],
		    controllerAs : 'store'
		};
	});
})();