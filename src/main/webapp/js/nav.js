/**
 * 
 */

(function() {
	var app = angular.module('navigation', ['cart', 'products']);
	
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