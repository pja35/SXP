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
})();
