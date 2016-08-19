/**
 * Navigation module, responsible for user navigation around the website.
 * It has the cart and products modules as the dependencies
 */

(function() {
	var app = angular.module('navigation', ['cart', 'products']);
	
	/*
	 * navController - Controller that is used to manipulate the loading of pages when tabs are clicked
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
})();