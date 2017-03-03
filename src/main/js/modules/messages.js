(function() {
	var module = angular.module('app.messages',  ['ui.directives','ui.filters']);
	module.config(function($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise("/");
		$stateProvider
		.state('messages', {
			url: '/messages',
			templateUrl: 'messages.html',
			controller: function($rootScope, $scope, $state, Message, User) {
				isUserConnected($rootScope, $scope, $state);
				$scope.app.configHeader({contextButton:'addMessage', title: 'Messages'});
				refresh();

				$scope.open = function(chat){ //Set a clicked active
					angular.element('.tab').removeClass('active');
					angular.element('#tab'+chat).addClass('active');
					angular.element('.well').removeClass('chatActive');
					angular.element('#'+chat).addClass('chatActive');
				}

				$scope.addMessage = function(chat, messageContent) {
					if(messageContent){
						//Message is available thanks to restApi.js
						var message = new Message({
							receiverName: chat,
	      					messageContent: messageContent
						});
						message.$save(function(){
								refresh();
						},function(err){
      						$scope.errorFields = true;
    					});
					}
				};

				function refresh(){ //Refresh request
					$scope.messages = [];
					$scope.chats = [];
					//User is available thanks to restApi.js
					$scope.user = User.get({
						id: $scope.app.userid
					});
					$scope.messages = Message.query(function(){
						var tmp = {};
						for(var i = 0; i < $scope.messages.length; i++){
							if($scope.messages[i].sendName != $scope.user.nick)
								tmp[$scope.messages[i].sendName] = 0;
							else 
								tmp[$scope.messages[i].receiverName] = 0;
						}
						for(var j in tmp){
							$scope.chats.push(j);
						}
					});
				}
			}
		})
		.state('addMessage', {
			url: '/messages/new',
			templateUrl: 'newMessage.html',
			controller: function($rootScope,$scope, $state, Message, User, $http) {
				isUserConnected($rootScope, $scope, $state);
				$scope.app.configHeader({contextButton:'', title: 'New message', back:'yes'});
				$scope.action = 'add'; //Specify to the template we are adding a message, since it the same template as the one for editing.
				//User is available thanks to restApi.js
				var currentUser = User.get({
					id: $scope.app.userid
				});
				$scope.submit = function() {
					if($scope.messageContent && $scope.receiverName){
						$scope.errorUsername = false;
						$scope.errorFields = false;
			            RESTAPISERVER = $scope.$parent.apiUrl;
			           	$http.get(RESTAPISERVER + "/api/users/").then(
		                function(response) {
		                    var userList = response.data;
		                    for(var i = 0; i < userList.length; i++){
	        					if($scope.receiverName == userList[i].nick && $scope.receiverName != currentUser.nick){
	        						var message = new Message({
										receiverName: $scope.receiverName,
				      					messageContent: $scope.messageContent
									});
									message.$save(function() {
										$state.go('messages');
										
									});
	        					}
        					}
        					if(!message)
	        					$scope.errorUsername = true;
		                });
					}
					else {
						$scope.errorFields = true;
					}
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
