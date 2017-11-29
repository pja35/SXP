(function () {
    var module = angular.module('app.contact', []);

    //This declares states, their routes, their appeareance, and gives the name of their controller
    module.config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('contact', {
                url: '/contact',
                templateUrl: 'contact.html',
                controller: 'contact'
            })
    });

    // 'about' state controller function
    module.controller('contact', function ($http, $rootScope, $scope, $state, Item, User) {
        isUserConnected($http, $rootScope, $scope, $state, User);


        //TODO configure the sending of the request
    });

})();

