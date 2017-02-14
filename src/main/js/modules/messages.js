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
							senderName: chat,
	      					body: messageContent
						});
						message.$save(function(){
							refresh();
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
							if($scope.messages[i].senderName != $scope.user.nick)
								tmp[$scope.messages[i].senderName] = 0;
							else 
								tmp[$scope.messages[i].receiversNames] = [];
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
					if($scope.messageContent && $scope.receiver){
						$scope.errorUsername = false;
						$scope.errorFields = false;
			            RESTAPISERVER = $scope.$parent.apiUrl;
			           	$http.get(RESTAPISERVER + "/api/users/").then(
		                function(response) {
		                    var userList = response.data;
		                    for(var i = 0; i < userList.length; i++){
	        					if($scope.receiver == userList[i].nick && $scope.receiver != currentUser.nick){
	        						var message = new Message({
	        							receiversNames: [$scope.receiver],
				      					body: $scope.messageContent
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
})();
