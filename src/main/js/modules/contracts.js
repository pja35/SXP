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
    });
    
    
    
    // 'View contract' state controller function
    module.controller('viewContract',  function($scope, $http, $stateParams, Contract, $state) {
	  $scope.app.configHeader({back: true, title: 'View contract', contextButton: 'editContract', contextId: $stateParams.id});
	  
	  var contract = Contract.get({id: $stateParams.id}, function() {
	    //Just load the contract and display it via the bindings with contract.html
	    $scope.contract = contract;
	    
	    $scope.title = contract.title
    	$scope.clauses = contract.clauses;
	    
	    // Get parties from the hashmap of names and id (to identify exactly a user)
	    $scope.parties = [];
	    pN = contract.partiesNames;
	    for (i=0; i<pN.length; i++){
	    	names = pN[i];
	    	$scope.parties[i] = names["value"] + " - " + names["key"];
	    }
	  });
	  
	  $scope.modify = function(){
		  $state.go("editContract", {id : contract.id});
	  };
	  
	  $scope.sign = function(){
		  $http.put(RESTAPISERVER + '/api/contracts/sign/:id', contract.id);
		  $state.go('viewContracts');
	  }
	  
	  $scope.decline = function(){
		  $http.put(RESTAPISERVER + '/api/contracts/cancel/:id', contract.id);
		  $state.go('viewContracts');
	  }
	  
	});
    
    
    module.controller('editContract', function($scope, $stateParams, Contract, $state, $http){
    	$scope.app.configHeader({back: true, title: 'Edit contracts', contextId: $stateParams.id});
    	$scope.action = 'edit';

		$scope.form = {};
    	$scope.userList =[];
    	getUsers($http, $scope);
    	
    	$scope.parties = [];
		var contract = Contract.get({id: $stateParams.id}, function() {
			//First, load the item and display it via the bindings with item-form.html
			$scope.form.title = contract.title
			$scope.clauses = contract.clauses;
		    pN = contract.partiesNames;
		    for (i=0; i<pN.length; i++){
		    	names = pN[i];
		    	$scope.parties[i] = names["value"] + " - " + names["key"];
		    }
		});
		
		
    	$scope.updateparties = function() {updateParties($scope)};
    	$scope.updateclauses= function() {updateClauses($scope)};

    	$scope.deleteParty = function(p){deleteParty($scope,p);};
    	$scope.deleteClause = function(c){deleteClause($scope,c);};

    	$scope.submit = function() {
    		
        	pN = $scope.parties;
        	partiesId = [];
    	    for (i=0; i<pN.length; i++){
    	    	names = pN[i];
    	    	partiesId[i] = names.split(" - ")[1];
    	    };
        	
    	    
    		if ($scope.form.addParty != null && $scope.form.addParty.length>2){updateParties($scope);}
    		if ($scope.form.addClause != null && $scope.form.addClause.length>2){updateClauses($scope);}
    		//Contract is available thanks to restApi.js
    		contract.title = $scope.form.title;
    		contract.clauses = $scope.clauses;
    		contract.parties = partiesId;
			
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
    
    
    
    module.controller('addContract', function($scope, Contract, $state, $http){
    	
    	$scope.app.configHeader({back: true, title: 'Add contracts'});
    	$scope.action = 'add';
    	
    	$scope.parties=[];
    	$scope.clauses=[];
    	$scope.userList =[];
    	
		getUsers($http, $scope);
		
    	$scope.updateparties = function() {updateParties($scope)};
    	$scope.updateclauses= function() {updateClauses($scope)};
    	
    	$scope.deleteParty = function(p){deleteParty($scope,p);};
    	$scope.deleteClause = function(c){deleteClause($scope,c);};
    	
    	$scope.submit = function() {

        	pN = $scope.parties;
        	partiesId = [];
    	    for (i=0; i<pN.length; i++){
    	    	names = pN[i];
    	    	partiesId[i] = names.split(" - ")[1];
    	    };
    	    
    		if ($scope.form.addParty != null && $scope.form.addParty.length>2){updateParties($scope);}
    		if ($scope.form.addClause != null && $scope.form.addClause.length>2){updateClauses($scope);}
    		var contract = new Contract({
	    		title : $scope.form.title,
    			clauses: $scope.clauses,
	    		parties: partiesId
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

function getUsers($http, $scope){
	$http.get(RESTAPISERVER + "/api/users/").then(
		function(response){
			var userList = response.data;
			$scope.userList = [];

			for(i=0; i<userList.length; i++){
				if (userList[i].nick != ""){
					$scope.userList[i] = { 'name' : userList[i].nick
							, 'id' : userList[i].id };
				}
			}
		}
	);
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