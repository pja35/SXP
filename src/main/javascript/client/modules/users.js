(function() {
	var module = angular.module('app.users', []);
	module.config(function($stateProvider, $urlRouterProvider) {
		$urlRouterProvider.otherwise("/");
		$stateProvider
		.state('subscribe', {
			url: '/subscribe',
			templateUrl: 'partials/accounts/subscribe.html',
			controller: 'subscribe'
		})
		.state('login', {
			url: '/login',
			templateUrl: 'partials/accounts/login.html',
			controller: 'login'
		})
		.state('logout', {
			url: '/logout',
			templateUrl: 'partials/accounts/logout.html',
			controller: 'logout'
		})
		.state('account', {
			url: '/account',
			templateUrl: 'partials/accounts/account.html',
			controller: 'account'
		});
	});

	module.controller('login', function($scope, $state, $http) {
		$scope.app.configHeader({title: "Login"});
		$scope.form = {};
		$scope.form.server = RESTAPISERVER;

		$scope.submit = function() {
			RESTAPISERVER = $scope.form.server;
			var user = $scope.form.login;
			var password = $scope.form.password;
			$http.get(RESTAPISERVER + "/api/users/login?login=" + user + "&password=" + password).then(function(response) {
					var obj = response.data;
					console.debug(obj);
					if(obj.error) {
						$scope.error = true;
					} else {
						$http.defaults.headers.common['Auth-Token'] = obj.token;
						$scope.app.userid = obj.userid;
						$state.go('myItemsView');
					}

			}, function(response) {

			});
		};
	});

	module.controller('account', function($scope, $state, $http, User) {
		$scope.app.configHeader({title: "Account"});
		var user = User.get({id: $scope.app.userid}, function() {
			$scope.user = user;
		});
	});

	module.controller('logout', function($scope, $state, $http) {
		$scope.app.configHeader({title: "logout"});
		$http.get(RESTAPISERVER + "/api/users/logout");
		$scope.app.setCurrentUser(null);
	});

	module.controller('subscribe', function($scope, $state, $http) {
		$scope.app.configHeader({title: "Subscribe", back: true});
		$scope.form = {};
		$scope.form.server = RESTAPISERVER;

		$scope.submit = function() {
			RESTAPISERVER = $scope.form.server;
			var user = $scope.form.login;
			var password  = $scope.form.password;
			$http.get(RESTAPISERVER + "/api/users/subscribe?login=" + user + "&password=" + password).then(function(response) {
				var data = response.data;
				$scope.app.setCurrentUser(data.token);
				$http.defaults.headers.common['Auth-Token'] = data.token;
				$scope.app.userid = data.userid;
				$state.go("myItemsView");
			}, function(response) {

			});
		};
	});

})();
