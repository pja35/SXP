(function() {
	var module = angular.module('app.contracts',[]);

	module.config(function($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise("/");
		$stateProvider
		
		.state('contracts', {
			url: '/contracts',
			templateUrl: 'contracts.html',
			controller: function($rootScope, $scope, $state) {
				isUserConnected($rootScope,$scope,$state);
				$scope.app.configHeader({contextButton:'addContract', title: 'Contracts'});
			}
		})
		
		.state('addContract', {
			url: '/contracts/add',
			templateUrl: 'newContract.html',
			controller: function($http, $rootScope, $scope, $state, User, Item) {
				isUserConnected($rootScope,$scope,$state);
				$scope.app.configHeader({back: true, title: 'New Contract'});
				var user = User.get({
            		id: $scope.app.userid
        		}, function() {
            		$scope.user = user;
        		});
        		
        		RESTAPISERVER = "http://localhost:8081";
        		$http.get(RESTAPISERVER + "/api/items/").then(
        			function(response){
        				$scope.items = response.data;
        				var list = document.getElementById("items-list");
        				for(i=0; i<$scope.items.length; i++){
        					var item = document.createElement("option");
        					item.value = $scope.items[i].title;
        					item.text = $scope.items[i].title;
        					list.appendChild(item);
        				}
        			});
        			
        		$http.get(RESTAPISERVER + "/api/users/").then(
        			function(response){
        				var userList = response.data;
        				$scope.userList = [];

        				for(i=0; i<userList.length; i++){
        					$scope.userList[i] = { 'name' : userList[i].nick };
        				}
        			}
        		);
        		
        		$scope.$watch("card", function(newValue, oldValue) {
					if ($scope.card.length > 0) {
					 	var result = document.getElementById("otheruser-items-list");
					 	while (result.firstChild) {
							result.removeChild(result.firstChild);
						}
						
						$http.get(RESTAPISERVER + "/api/users/").then(
							function(response){
								var itemList = response.data;
								console.debug(itemList);
							}
						);
						var item = document.createElement("option");
						item.value = $scope.card;
						item.text = $scope.card;
						result.appendChild(item);
					}
			  });
        		
        		$scope.submit = function (){
        			//TODO add Contract to SGBD
        			$state.go("contracts");
        		}
			}
		});
	});
})();
