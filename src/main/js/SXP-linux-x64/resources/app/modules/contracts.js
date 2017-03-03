(function() {
    var module = angular.module('app.contracts', []);
    module.config(function($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise("/"); //if someone messes up the url, go back home
        $stateProvider
	        .state('viewContracts', {
	            url: '/contracts',
	            templateUrl: 'contracts/contracts.html',
	            controller: 'viewContracts'
	        })
	        .state('viewContract', {
	            url: '/contracts/view/:id',
	            templateUrl: 'contracts/contract.html',
	            controller: 'viewContract'
	        })
		    .state('editContract', {
		        url: '/contracts/edit/:id',
		        templateUrl: 'contracts/contract-form.html',
		        controller: 'editContract'
		    })
		    .state('addContract', {
		        url: '/contracts/add',
		        templateUrl: 'contracts/contract-form.html',
		        controller: 'addContract'
		    });
    });
    
    
    // 'contracts' state controller function
    module.controller('viewContracts', function($scope, Contract) {
    	//TODO: do some of this with configHeader:
    	$scope.app.configHeader({back: false, title: 'Contracts', contextButton: 'addContract'});
  	  
    	$scope.contracts = [];
    	$scope.contracts = Contract.query(); //Fetch contracts, thanks to restApi.js
    	//The bindings with contracts.html will display them automatically
    	$scope.getclauses = function(c){
    		c.clauses = JSON.parse(c.clauses);
    	}
    });
    
    
    
    // 'View contract' state controller function
    module.controller('viewContract',  function($scope, $stateParams, Contract, $state) {
	  $scope.app.configHeader({back: true, title: 'View contract', contextButton: 'editContract', contextId: $stateParams.id});
	  var contract = Contract.get({id: $stateParams.id}, function() {
	    //Just load the contract and display it via the bindings with contract.html
	    $scope.contract = contract;
	    
	    $scope.title = contract.title
    	$scope.clauses = JSON.parse(contract.clauses);
    	$scope.parties = JSON.parse(contract.parties);
	  });
	  $scope.modify = function(){
		  $state.go("editContract", {id : contract.id});
	  }
	});
    
    
    
    module.controller('editContract', function($scope, $stateParams, Contract, $state){
    	$scope.app.configHeader({back: true, title: 'Edit contracts', contextId: $stateParams.id});
    	$scope.action = 'edit';

		$scope.form = {};
		var contract = Contract.get({id: $stateParams.id}, function() {
			//First, load the item and display it via the bindings with item-form.html
			$scope.form.title = contract.title
			$scope.clauses = JSON.parse(contract.clauses);
			$scope.parties = JSON.parse(contract.parties);
		});

    	$scope.updateparties = function() {updateParties($scope)};
    	$scope.updateclauses= function() {updateClauses($scope)};

    	$scope.deleteParty = function(p){deleteParty($scope,p);};
    	$scope.deleteClause = function(c){deleteClause($scope,c);};

    	
    	$scope.submit = function() {
    		if ($scope.form.addParty != null && $scope.form.addParty.length>2){updateParties($scope);}
    		if ($scope.form.addClause != null && $scope.form.addClause.length>2){updateClauses($scope);}
    		//Contract is available thanks to restApi.js
    		contract.title = $scope.form.title;
    		contract.clauses = JSON.stringify($scope.clauses);
    		contract.parties = JSON.stringify($scope.parties);
			
    		contract.$update(function() {
    			$state.go('viewContracts');
    	    });
    	};
    	
    	$scope.delete = function(){
    		contract.$delete(function(){
    			 $state.go('viewContracts');
    		})
    	}
    });
    
    
    
    module.controller('addContract', function($scope, Contract, $state){
    	
    	$scope.app.configHeader({back: true, title: 'Add contracts'});
    	$scope.action = 'add';
    	
    	$scope.parties=[];
    	$scope.clauses=[];
    	
    	$scope.updateparties = function() {updateParties($scope)};
    	$scope.updateclauses= function() {updateClauses($scope)};
    	
    	$scope.deleteParty = function(p){deleteParty($scope,p);};
    	$scope.deleteClause = function(c){deleteClause($scope,c);};

    	$scope.submit = function() {
    		if ($scope.form.addParty != null && $scope.form.addParty.length>2){updateParties($scope);}
    		if ($scope.form.addClause != null && $scope.form.addClause.length>2){updateClauses($scope);}
    		var contract = new Contract({
	    		title : $scope.form.title,
    			clauses: JSON.stringify($scope.clauses),
	    		parties: JSON.stringify($scope.parties)
			});
    		// Create the contract in the database thanks to restApi.js
    		contract.$save(function() {
				$state.go('viewContracts');
			});
    	};
    });


  //directives define new html tags or attributes; think of them as macros.
  module.directive('contract', function() {
    return {
      restrict: 'E',
      templateUrl: 'contracts/one-contract.html' //TODO: rename this to item-one
    };
  });
  module.directive('party', function() {
    return {
      restrict: 'E',
      templateUrl: 'contracts/party.html'
    };
  });
  module.directive('clause', function() {
	    return {
	      restrict: 'E',
	      templateUrl: 'contracts/clause.html'
	    };
	  });
 
})();

function deleteClause($scope, clause){
	var index = $scope.clauses.indexOf(clause);
	if (index > -1){
		$scope.clauses.splice(index, 1);
	}
}function updateClauses($scope){
	var clause = $scope.fromparty +" gives "+ $scope.form.addClause +" to "+ $scope.forparty;
	var index = $scope.clauses.indexOf(clause);
	if (index == -1){
		$scope.clauses.push(clause);
		$scope.form.addClause="";
	}
	return false;
}

function deleteParty($scope, p){
	var index = $scope.parties.indexOf(p);
	if (index > -1){
		$scope.parties.splice(index, 1);
	}
}function updateParties($scope){
	var index = $scope.parties.indexOf($scope.form.addParty);
	if (index == -1){
		$scope.parties.push($scope.form.addParty);
		$scope.form.addParty="";
	}
}
	/* var module = angular.module('app.contracts',[]);

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
})();*/
