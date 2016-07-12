(function() {
	var module = angular.module('messages', []);
	module.config(function($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise("/");
		$stateProvider
		.state('messages', {
			url: '/messages',
			templateUrl: 'messages.html',
			controller: function($scope) {
				$scope.app.setContextButton('addMessage');
				$scope.app.setTitle('Messages');
				$scope.app.setBackUrl(null);
			}
		})
		.state('addMessage', {
			url: '/messages/new',
			templateUrl: 'newMessage.html',
			controller: function($scope) {
				$scope.app.setContextButton('');
				$scope.app.setTitle('New message');
				$scope.app.setBackUrl("yes");
			}
		});
	});
})();