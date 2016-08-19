(function() {
    var module = angular.module('services.rest', []);

    //This module creates other modules
    //Used to handle backend resources
    //In this concise way we have e.g.
    //  User.get() that will fetch a user from backend
    //  user.$save() to then update it
    // see https://docs.angularjs.org/api/ngResource/service/$resource


    module.factory('User', function($resource) {
        return $resource(RESTAPISERVER + '/api/users/:id',
            //Just specified the route to access the resource.
            //The :id will become and id=... if a value for it is specified
            {
                id: '@id'
            },
            //Just specified where to get this value from.
            //E.g. if calling User.get(obj), if obj has attribute id, take its value.
            {
                update: {
                    method: 'PUT'
                }
            });
    });


    module.factory('Item', function($resource) {
        return $resource(RESTAPISERVER + '/api/items/:id', {
            id: '@id'
        }, {
            update: {
                method: 'PUT'
            }
        });
    });


})();
