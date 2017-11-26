(function() {

    var module = angular.module('app', ['ngMaterial', 'ngMessages','ngTagsInput','ui.router', 'ui.bootstrap', 'ngAnimate', 'ngResource', 'services.rest', 'app.myItems', 'search', 'app.messages', 'app.users', 'app.settings', 'ngOboe', 'app.contracts', 'app.contact']);
    //app is the name of this module, what follows are depencies
    //ui.router is standard, for routing
    //ngOboe is standard to stream the results of the searches
    //ngResource is standard to facilitate rest API requests
    //services.rest is custom, it uses ngResource
    //the others are the custom modules corresponding to each page



    //THE MODEL
    module.controller('appController', function($rootScope) {
        //This is the controller for the entire GUI
        var self = this; //to see the controller within its methods
        $rootScope.apiUrl = RESTAPISERVER; //where the server is
        this.backUrl = null; //whether to display back button
        this.contextButton = null; //whether to display contextButton and with what role
        this.title = "SXP network"; //current page title
        this.currentUser = null; //current, authentified, user
        this.contextId = 0; //just to carry a number when needed
        //Boring setters and getters
        this.setCurrentUser = function(user) {
            self.currentUser = user;
        }
        this.getCurrentUser = function() {
            return self.currentUser;
        }
        this.setBackUrl = function(url) {
            self.backUrl = url;
        }
        this.getBackUrl = function() {
            return self.backUrl;
        }
        this.showBackUrl = function() {
            return self.backUrl != null;
        }
        this.setTitle = function(title) {
            self.title = title;
        }
        this.getTitle = function() {
            return self.title;
        }
        this.setContextId = function(id) {
            self.contextId = id;
        }
        this.getContextId = function() {
            return self.contextId;
        }
            //GUI togglers
        this.showContextButton = function(button) {
            return button === self.contextButton;
            //to display the right kind of contextButton according to situation, see contextButtons.html
        };
        this.setContextButton = function(button) {
            self.contextButton = button;
        };
        this.configHeader = function(obj) {
            //Tunes the top bar
            if (obj.hasOwnProperty('back')) {
                self.setBackUrl(obj.back == true ? "true" : null);
                //if obj says it wants the back button, set to true
                //if it says random stuff set to null
                //else set to null
            } else {
                self.setBackUrl(null);
            }
            if (obj.hasOwnProperty('title')) {
                self.setTitle(obj.title);
            } else {
                self.setTitle("SXP network");
            }
            if (obj.hasOwnProperty('contextButton')) {
                self.setContextButton(obj.contextButton);
            } else {
                self.setContextButton(null);
            }

            if (obj.hasOwnProperty('contextId')) {
                self.setContextId(obj.contextId);
            }
        };
    });

    //THE HOME CONTROLLER
    module.config(function($stateProvider, $urlRouterProvider) {
        //This declares the state 'home' together with its route, appeareance, and controller function
        $stateProvider
            .state('home', {
                url: '/', //route
                templateUrl: 'home.html', //appeareance
                controller: function($scope, $state) { //controller function
                    $scope.app.configHeader({contextButton:'search'});
                    if ($scope.app.getCurrentUser() == null) {
                        $state.go("login"); //go to the login state defined in users.js
                    }
                }
            });
    });


    // USEFUL HELPERS ALL ACROSS APP

    module.filter('hex', function() {
        //A filter serves to format data, with a pipe, within html
        //this one is to display public keys
        return function(input) {
            return Number(input).toString(16);
        };
    });

    //Directives define new html tags or attributes; think of them as macros.
    module.directive('navbar', function() {
        return {
            restrict: 'E', //this is an html tag
            templateUrl: 'navbar.html' //described here
        };
    });

    module.directive('sidemenu', function() {
        return {
            restrict: 'E',
            templateUrl: 'sidemenu.html'
        };
    });

    module.directive('contextButton', function() {
        return {
            restrict: 'E',
            templateUrl: 'contextButtons.html'
        };
    });

})();

/**
 * This function was udpated to save users session on sessionStorage. This way user can refresh the page without
 * losing is session and being forced to loging again
 * @param $http
 * @param $rootScope
 * @param $scope
 * @param $state
 * @param User
 */
function isUserConnected($http, $rootScope, $scope, $state, User) {
    if (sessionStorage.getItem("curUser") != null) {
        $http.defaults.headers.common['Auth-Token'] = sessionStorage.getItem("token");
        $rootScope.userLogged = true;
        var user = User.get({
            id: sessionStorage.getItem("curUser")
        }, function () {
            $scope.user = user;
            $scope.app.userid = sessionStorage.getItem("curUser");
            $scope.app.setCurrentUser(user);
            $scope.app.nick = $scope.userNick;

            console.log("User connected " + $scope.app.userid);
        });

    }
    else {
        $rootScope.userLogged = false;
        sessionStorage.clear();
        console.log("user not connected  redirect->login");
        $state.go('login');
    }
}
