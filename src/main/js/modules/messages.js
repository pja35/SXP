(function() {
	var module = angular.module('app.messages',  ['ui.directives','ui.filters']);
	module.config(function($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise("/");
		$stateProvider
		.state('messages', {
			url: '/messages',
			templateUrl: 'messages.html',
			controller: function($rootScope,$scope, $state,$stateParams, Message, User, $http,Oboe) {
				isUserConnected($rootScope, $scope, $state);
				$scope.app.configHeader({contextButton:'addMessage', title: 'Messages'});
				
				$scope.stream = null; //The stream of async results
				
				$scope.user = User.get({
					id: $scope.app.userid
				});
				
				//refresh();
				
				loadMessages();
				
				$scope.open = function(chat){ //Set a clicked active
					angular.element('.tab').removeClass('active');
					angular.element('#tab'+chat).addClass('active');
					angular.element('.well').removeClass('chatActive');
					angular.element('#'+chat).addClass('chatActive');
				}

				$scope.addMessage = function(chatName,chatId, messageContent) {
					
					if(messageContent){
						$scope.searchMessages = true;
						//Message is available thanks to restApi.js
						var message = new Message({
							receiverName: chatName,
							receiverId: chatId,
	      					messageContent: messageContent
						});
						console.log("add message");
						console.log(message);
						Oboe({
	                        url: RESTAPISERVER + "/api/messages/",
	                        method: 'POST',
	                        body : message,
	                        withCredentials: true,
	                        headers: {'Auth-Token' : $http.defaults.headers.common['Auth-Token']},
	                        start: function(stream) {
	                            // handle to the stream
	                            $scope.stream = stream;
	                            $scope.status = 'started';
	                            
	                        },
	                        done: function(parsedJSON) {
	                            $scope.status = 'done';
	                            
	                        }
	                    }).then(function() {
	                    }, function(error) {
	                    	$scope.searchMessages = false;
	                    }, function(node) {
	                        if (node != null && node.length != 0) { 
	                                console.log(node);
	                                $state.reload();
	                                //refresh();
	                                //$state.go('messages');
	                               $scope.searchMessages = false;
	                        }
	                    });
					}
				};

				function refresh(){ //Refresh request
					var tmp = {};
					$scope.chats = [];
					for(var i = 0; i < $scope.messages.length; i++){
						if($scope.messages[i].senderName != $scope.user.nick)
							tmp[$scope.messages[i].senderName] = $scope.messages[i].senderId;
						else 
							tmp[$scope.messages[i].receiverName] = $scope.messages[i].receiverId;
					}
					for(var j in tmp){
						$scope.chats.push({name:j,id:tmp[j]});
					}
				}
				
				function loadMessages(){ 
					$scope.messages = [];
					$scope.chats = [];
                    
                    $scope.searchMessages = true;
                    
                    if ($scope.stream != null) {
                        $scope.stream.abort();
                    }
                    Oboe( 
                    	{
                        url: RESTAPISERVER + "/api/messages/",
                        pattern:'!',
                        withCredentials: true,
                        headers: {'Auth-Token' : $http.defaults.headers.common['Auth-Token']},
                        start: function(stream) {
                            // handle to the stream
                            $scope.stream = stream;
                            $scope.status = 'started';
                            $scope.searchMessages = true;
                        },
                        done: function(parsedJSON) {
                            $scope.status = 'done';
                            $scope.searchMessages = false;
                        }
                    }).then(function() {
                    }, function(error) {
                    }, function(node) {
                        if (node != null && node.length != 0) { 
                            for (var i = 0; i < node.length; i++) {
                                console.log(node[i]);
                                $scope.messages.push(node[i]);	
                            }
                        }
                        refresh();
                    });
                }
                
			}
		})
		.state('addMessage', {
			url: '/messages/new',
			templateUrl: 'newMessage.html',
			controller: function($rootScope,$scope, $state,$stateParams, Message, User, $http,Oboe) {
				isUserConnected($rootScope, $scope, $state);
				$scope.app.configHeader({contextButton:'', title: 'New message', back:'yes'});
				$scope.action = 'add'; //Specify to the template we are adding a message, since it the same template as the one for editing.
				
				$scope.results = []; //The currently received and displaid results
                $scope.stream = null; //The stream of async results

                $scope.pushResult = function($obj) { //to add an object in the result list
                    if ($obj == null) return; //check if valid
                    for (var i = 0; i < $scope.results.length; i++) { //check if not already there
                        if ($scope.results[i].id == $obj.id) return;
                    }
                    $scope.results.push($obj); //OK, add
                }
				
				//User is available thanks to restApi.js
				var currentUser = User.get({
					id: $scope.app.userid
				});
				$scope.submit = function() {
					if($scope.messageContent && $scope.receiverName && $scope.receiverId){
						$scope.errorUsername = false;
						$scope.errorFields = false;
						$scope.sendMessage = true;
						var message = new Message({
							receiverName: $scope.receiverName,
							receiverId: $scope.receiverId,
	      					messageContent: $scope.messageContent
						});
						
						Oboe({
		                        url: RESTAPISERVER + "/api/messages/",
		                        method: 'POST',
		                        body : message,
		                        withCredentials: true,
		                        headers: {'Auth-Token' : $http.defaults.headers.common['Auth-Token']},
		                        start: function(stream) {
		                            // handle to the stream
		                            $scope.stream = stream;
		                            $scope.status = 'started';
		                            $scope.sendMessage = true;
		                        },
		                        done: function(parsedJSON) {
		                            $scope.status = 'done';
		                            $scope.sendMessage = false;
		                        }
		                    }).then(function() {
		                    }, function(error) {
		                    	$scope.sendMessage = false;
		                    }, function(node) {
		                        if (node != null && node.length != 0) {
		                        		$scope.sendMessage = false;
		                                console.log(node);
		                                $state.go('messages');
		                        }
		                    });
					}
					else {
						$scope.errorFields = true;
					}
				};
				
				$scope.userAutoComplete = function() { 
                    $scope.results = [];
                    $scope.errorSearch = false;
                    $scope.searchUser = true;
                    $scope.hideAfterSelected = true;
                    $scope.errorFields = false;
                    $scope.receiverId = null;
                    $scope.receiverPbkey = null;
                    
                    if ($scope.stream != null) {
                        $scope.stream.abort();
                    }
                    Oboe( 
                    	{
                        url: RESTAPISERVER + "/api/search/users?nick=" + $scope.receiverName,
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
                            }
                        }
                        
                        $scope.searchUser = false;
                        console.log($scope.results.length);
                        if($scope.results.length == 0)
                        	$scope.errorSearch = true;
                        else
                        	$scope.errorSearch = false;
                        
                    });
                };
                
                
                $scope.selectUser = function($stateParams) { 
                    $scope.hideAfterSelected = false;
                    $scope.receiverId = $stateParams.receiver.id;
                    $scope.receiverPbkey = $stateParams.receiver.key.publicKey;
                };
                
                
			}
		});
	});
	
	module.controller('display', function($scope){
		//user : person we are talking to
		
		var currentUser=0;

		//TODO: Make this automatic with a connexion to the server
		//Show 		: tells us what conversation to print on screen
		//id 		: useful to toggle the view
		//activity 	: "active" for the toggled viem, "" otherwise
		//mail 		: an array of messages ordered by date decreasingly
		this.users=[
			{"show":true,"id":0,"name":"Bob", "activity":"active", "mails":[
					{"sender":"Me","date":"3 hours ago","text":"carrot ?"},
					{"sender":"Bob","date":"2 hours ago","text":"potatoes ?"}
				]},
			{"show":false,"id":1,"name":"Alice", "activity":"", "mails":[
					{"sender":"Alice","date":"1 hours ago","text":"You there?"}
				]},
			{"show":false,"id":2,"name":"User 3", "activity":"", "mails":[
					{"sender":"User 3","date":"1 day","text":"Hello, I'd like to buy you some potatoes pls"},
					{"sender":"Me","date":"4 hours ago","text":"Sure, how much do you want ?"},
					{"sender":"User 3","date":"1 hour ago","text":"Nice, about 2kg pls."}
				]},		 
		];
		
		//Function that changes the view when we change the conversation is clicked
		this.toggleUserActivity = function(j){
			this.users[currentUser].activity="";
			this.users[currentUser].show=false;
			currentUser=j;
			this.users[j].activity="active";
			this.users[j].show=true;
		}
	});
	
})();
