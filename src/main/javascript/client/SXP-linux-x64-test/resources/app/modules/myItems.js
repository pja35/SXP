(function() {
  var module = angular.module('app.myItems', []);

  // ---- Module state configuration
  module.config(function($stateProvider, $urlRouterProvider) {
    $stateProvider
    .state('myItemsView', {
      url: '/myitems',
      templateUrl: 'partials/items/items.html',
      controller: 'viewItems'
    })
    .state('myItemsViewOne', {
      url: '/myitems/view/:id',
      templateUrl: 'partials/items/item.html',
      controller: 'viewItem'
    })
    .state('myItemsAdd', {
      url: '/myitems/add',
      templateUrl: 'partials/items/item-form.html',
      controller: 'addItem'
    })
    .state('myItemsEdit', {
      url: '/myitems/edit/:id',
      templateUrl: 'partials/items/item-form.html',
      controller: 'editItem'
    });
  });

// ---- View items controller
module.controller('viewItems', function($scope, Item) {
  $scope.app.setBackUrl(null);
  $scope.app.setContextButton('addItem');
  $scope.app.setTitle('Items');
  $scope.items = [];
  $scope.items = Item.query();
});

// ---- Add item controller
module.controller('addItem', function($scope, Item, $state) {
  $scope.app.configHeader({back:true, title: 'Add item'});
  $scope.action = 'add'; //specify to the template we are adding an item
  $scope.submit = function() {
    var item = new Item({
      title: $scope.form.title,
      description: $scope.form.description
    });
    item.$save(function() {
      $state.go('myItemsView');
    });
  };
});


// ---- Edit item controller
module.controller('editItem', function($scope, $stateParams, Item, $state) {
  $scope.app.configHeader({back: true, title: 'Edit item'});
  $scope.action = 'edit';
  $scope.form = {};
  var item = Item.get({id: $stateParams.id}, function() {

    $scope.form.title = item.title;
    $scope.form.description = item.description;
  });

  $scope.submit = function() {
    item.title = $scope.form.title;
    item.description = $scope.form.description;
    item.$update(function() {
      $state.go('myItemsView');
    });
  };

  $scope.delete = function() {
    item.$delete(function() {
      $state.go('myItemsView');
    });
  };
});



// ---- View item controller
module.controller('viewItem', function($scope, $stateParams, Item) {
  $scope.app.configHeader({back: true, title: 'View item', contextButton: 'editItem', contextId: $stateParams.id});
  var item = Item.get({id: $stateParams.id}, function() {
    $scope.title = item.title;
    $scope.description = item.description;
    $scope.createdAt = item.createdAt;
  });
});


// ---- Item directive
module.directive('item', function() {
  return {
    restrict: 'E',
    templateUrl: 'partials/items/item-list.html'
  };
});
})();
