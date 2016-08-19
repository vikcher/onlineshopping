/**
 * Module responsible for the 'Products' tab
 * It has no dependencies
 */
(function(){
	var app = angular.module('products',[]);
	
	/*
	 * Custom element directive to list all the items in the catalog
	 * The controller has methods to get the product list and adding products to cart using the REST APIs
	 */
	app.directive('productItem', function() {
		return {
			restrict: 'E',
		    templateUrl : "product-item.html",
		    controller : ['$http', function($http) {
		    	var store = this;
				store.products = [];
				
				this.getProductList = function () {
					$http({method : 'GET', url : "https://vast-everglades-25484.herokuapp.com/rest/products"}).then(function successCallback(response){
						store.products = response.data.products;
						console.log(response.data.products);
					}, function errorCallBack(response) {

					});
				};
				
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