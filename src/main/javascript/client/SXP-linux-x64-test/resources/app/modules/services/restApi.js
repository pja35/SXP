(function() {
  var module = angular.module('services.rest', []);
  module.factory('Item', function($resource) {
    return $resource(RESTAPISERVER + '/api/items/:id', {id: '@id'}, {
      update: {
        method: 'PUT'
      }
    });
  });

  module.factory('User', function($resource) {
    return $resource(RESTAPISERVER + '/api/users/:id', {id: '@id'}, {
      update: {
        method: 'PUT'
      }
    });
  });

})();
