(function() {
  var module = angular.module('app.myItems', []);

  //This declares states, their routes, their appeareance, and gives the name of their controller
  module.config(function($stateProvider, $urlRouterProvider) {
    $stateProvider
    .state('myItemsView', {
      url: '/myitems',
      templateUrl: 'items/items.html',
      controller: 'viewItems'
    })
    .state('myItemsViewOne', {
      url: '/myitems/view/:id',
      templateUrl: 'items/item.html',
      controller: 'viewItem'
    })
    .state('myItemsAdd', {
      url: '/myitems/add',
      templateUrl: 'items/item-form.html',
      controller: 'addItem'
    })
    .state('myItemsEdit', {
      url: '/myitems/edit/:id',
      templateUrl: 'items/item-form.html',
      controller: 'editItem'
    });
  });

// 'View items' state controller function
module.controller('viewItems', function($scope, Item) {
  //TODO: do some of this with configHeader:
  $scope.app.setBackUrl(null);
  $scope.app.setTitle('Items');

  $scope.app.setContextButton('addItem');

  $scope.items = [];
  $scope.items = Item.query(); //Fetch items, thanks to restApi.js
  //The bindings with items.html will display them automatically
});

// 'Add item' state controller function
module.controller('addItem', function($scope, Item, $state) {
  $scope.app.configHeader({back:true, title: 'Add item'});
  $scope.action = 'add'; //Specify to the template we are adding an item, since it the same template as the one for editing.
  $scope.submit = function() {
    //Item is available thanks to restApi.js
    var item = new Item({
      title: $scope.form.title,
      description: $scope.form.description
    });
    item.$save(function() {
      $state.go('myItemsView');
    });
  };
});


// 'Edit item' state controller function
module.controller('editItem', function($scope, $stateParams, Item, $state) {
  $scope.app.configHeader({back: true, title: 'Edit item'});
  $scope.action = 'edit';
  $scope.form = {};
  var item = Item.get({id: $stateParams.id}, function() {
    //First, load the item and display it via the bindings with item-form.html
    $scope.form.title = item.title;
    $scope.form.description = item.description;
  });

  $scope.submit = function() {
    item.title = $scope.form.title;
    item.description = $scope.form.description;
    //Upon submission, modify the item
    //thanks to restApi.js
    item.$update(function() {
      $state.go('myItemsView');
    });
  };

  $scope.delete = function() {
    //TODO: is there a button for that?
    item.$delete(function() {
      $state.go('myItemsView');
    });
  };
});

// 'View item' state controller function
module.controller('viewItem', function($scope, $stateParams, Item) {
  $scope.app.configHeader({back: true, title: 'View item', contextButton: 'editItem', contextId: $stateParams.id});
  var item = Item.get({id: $stateParams.id}, function() {
    //Just load the item and display it via the bindings with items.html
    $scope.item = item;
  });
});


//directives define new html tags or attributes; think of them as macros.
module.directive('item', function() {
  return {
    restrict: 'E',
    templateUrl: 'items/item-list.html' //TODO: rename this to item-one
  };
});
})();
