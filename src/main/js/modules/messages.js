(function() {
	var module = angular.module('messages', []);

	//STILL TO BE IMPLEMENTED

	module.config(function($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise("/");
		$stateProvider
		.state('messages', {
			url: '/messages',
			templateUrl: 'messages.html',
			controller: function($scope) {
				//TODO use configHeader for that:
				$scope.app.setContextButton('addMessage');
				$scope.app.setTitle('Messages');
				$scope.app.setBackUrl(null);
			}
		})
		.state('addMessage', {
			url: '/messages/new',
			templateUrl: 'newMessage.html',
			controller: function($scope) {
				//TODO use configHeader for that:
				$scope.app.setContextButton('');
				$scope.app.setTitle('New message');
				$scope.app.setBackUrl("yes");
			}
		});
	});
	
	module.controller('display', function($scope){
		//user : person we are talking to
		
		var currentUser=0;

		//TODO: Make this automatic with a connexion to the server
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
