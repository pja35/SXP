(function() {
    var module = angular.module('search', []);



    module.config(function($stateProvider, $urlRouterProvider) {
        //This declares the state 'search' together with its route, appeareance, and controller function
        $urlRouterProvider.otherwise("/");
        $stateProvider
            .state('search', {
                url: '/search',
                templateUrl: 'search.html',
                controller: function($rootScope, $scope, $http, $state, Oboe) {
                    $scope.app.configHeader({title: 'Search'});

                    $scope.results = []; //The currently received and displaid results
                    $scope.stream = null; //The stream of async results

                    $scope.pushResult = function($obj) { //to add an object in the result list
                        if ($obj == null) return; //check if valid
                        for (var i = 0; i < $scope.results.length; i++) { //check if not already there
                            if ($scope.results[i].id == $obj.id) return;
                        }
                        $scope.results.push($obj); //OK, add
                    }

                    $scope.search = function() { //The function that launches the search
                        $scope.results = [];
                        $scope.errorSearch = false;
                        $scope.searchItem = true;
                        if ($scope.stream != null) { //Abort currently running search
                            $scope.stream.abort();
                        }
                        Oboe( //Launch async search stream following standard Oboe syntax
														{
                            url: RESTAPISERVER + "/api/search/simple?title=" + $scope.research,
                            pattern: '!',
                            start: function(stream) {
                                // handle to the stream
                                $scope.stream = stream;
                                $scope.status = 'started';
                            },
                            done: function(parsedJSON) {
                                $scope.status = 'done';
                            }
                        }).then(function() {
                            // promise is resolved
                        }, function(error) {
                            // handle errors
                        }, function(node) { //A node is just a partial list of matches from the streamed search
                            // node received
                            if (node != null && node.length != 0) { // if not empty

                                for (var i = 0; i < node.length; i++) { // push it to results
                                    console.log(node[i]);
                                    $scope.pushResult(node[i]);
                                    $scope.searchItem = false;
                                }

                            }
                            
                            console.log($scope.results.length);
                            if($scope.results.length == 0)
                            	$scope.errorSearch = true;
                            else
                            	$scope.errorSearch = false;
                            	
                            if ($scope.results.length === 1000 || node == null || node.length == 0) { //Abort the search when too many matches, or none left
                                $scope.stream.abort();
                                $scope.stream = null;
                                $scope.searchItem = false;
                            }
                        });

                        /*$http.get(RESTAPISERVER + "/api/search/simple?title=" + $scope.research).then(function(response) {
                        	$scope.results = response.data;
                        }, function(response) {

                        });*/
                    }

                }
            })
            
            .state('searchViewItem', {
			  url: '/search/view/:id',
			  templateUrl: 'items/item.html',
			  controller: function($rootScope,$scope, $state, $stateParams, Item) {
				  var item = Item.get({id: $stateParams.id}, function() {
				    $scope.app.configHeader({back: true, title: 'Search : '+item.title, contextButton: 'null', contextId: $stateParams.id});
					//Just load the item and display it via the bindings with items.html
					$scope.item = item;
				  });
				}
			});
    });
})();
