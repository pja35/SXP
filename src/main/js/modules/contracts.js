(function() {
    var module = angular.module('app.contracts', []);
    module.config(function($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise("/"); //if someone messes up the url, go back home
        $stateProvider
	        .state('contracts', {
	            url: '/contracts',
	            templateUrl: 'contracts/contracts.html',
	            controller: 'viewContracts'
	        })
	        .state('contractsViewOne', {
	            url: '/contracts/view/:id',
	            templateUrl: 'contracts/contract.html',
	            controller: 'viewContract'
	        })
		    .state('editContract', {
		        url: '/contracts/edit/:id',
		        templateUrl: 'contracts/contract-form.html',
		        controller: 'editContract'
		    });
    });
    
    // 'contracts' state controller function
    module.controller('viewContracts', function($scope, Contract) {
      //TODO: do some of this with configHeader:
      $scope.app.setBackUrl(null);
      $scope.app.setTitle('Contract');

      $scope.contracts = [];
      $scope.contracts = Contract.query(); //Fetch contracts, thanks to restApi.js
      //The bindings with contracts.html will display them automatically
    });
    
    // 'View contract' state controller function
    module.controller('viewContract',  function($scope, $stateParams, Contract) {
	  $scope.app.configHeader({back: true, title: 'View contract', contextButton: 'editContract', contextId: $stateParams.id});
	  var contract = Contract.get({id: $stateParams.id}, function() {
	    //Just load the contract and display it via the bindings with contract.html
	    $scope.contract = contract;
	  });
	});
    
    
    


  //directives define new html tags or attributes; think of them as macros.
  module.directive('contract', function() {
    return {
      restrict: 'E',
      templateUrl: 'contracts/contract-list.html' //TODO: rename this to item-one
    };
  });
 
})();
