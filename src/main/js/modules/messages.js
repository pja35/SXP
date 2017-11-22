(function () {
    var module = angular.module('app.messages', ['ui.directives', 'ui.filters']);
    module.config(function ($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise("/");
        $stateProvider
            .state('messages', {
                url: '/messages',
                templateUrl: 'messages.html',
                controller: function ($rootScope, $scope, $state, $stateParams, Message, User, $http, Oboe) {
                    isUserConnected($http, $rootScope, $scope, $state, User);
                    $scope.app.configHeader({contextButton: 'addMessage', title: 'Messages'});
                    $scope.openMessage = function (evt, cityName) {
                        // Declare all variables
                        var i, tabcontent, tablinks;

                        // Get all elements with class="tabcontent" and hide them
                        tabcontent = document.getElementsByClassName("tabcontent");
                        for (i = 0; i < tabcontent.length; i++) {
                            tabcontent[i].style.display = "none";
                        }

                        // Get all elements with class="tablinks" and remove the class "active"
                        tablinks = document.getElementsByClassName("tablinks");
                        for (i = 0; i < tablinks.length; i++) {
                            tablinks[i].className = tablinks[i].className.replace(" active", "");
                        }
						console.log(cityName);
                        // Show the current tab, and add an "active" class to the link that opened the tab
                        document.getElementById(cityName).style.display = "block";
                        evt.currentTarget.className += " active";
                    }
                    $scope.stream = null; //The stream of async results

                    $scope.user = User.get({
                        id: $scope.app.userid
                    });
                    //refresh();
                    loadMessages();

                    $scope.open = function (chat) { //Set a clicked active
                        angular.element('.tab').removeClass('active');
                        angular.element('#tab' + chat).addClass('active');
                        angular.element('.well').removeClass('chatActive');
                        angular.element('#' + chat).addClass('chatActive');
                    }

                    $scope.addMessage = function (chatName, chatId, messageContent) {
                        if (messageContent) {
                            $scope.searchMessages = true;
                            //Message is available thanks to restApi.js
                            var message = new Message({
                                receivers: chatId.receivers,
                                receiversNicks: chatId.receiversNicks,
                                messageContent: messageContent,
                                chatGroup: chatId.receivers.length > 1
                            });
                            console.log("add message");
                            console.log(message);
                            Oboe({
                                url: RESTAPISERVER + "/api/messages/",
                                method: 'POST',
                                body: message,
                                withCredentials: true,
                                headers: {'Auth-Token': $http.defaults.headers.common['Auth-Token']},
                                start: function (stream) {
                                    // handle to the stream
                                    $scope.stream = stream;
                                    $scope.status = 'started';

                                },
                                done: function (parsedJSON) {
                                    $scope.status = 'done';

                                }
                            }).then(function () {
                            }, function (error) {
                                $scope.searchMessages = false;
                            }, function (node) {
                                if (node != null && node.length != 0) {
                                    console.log(node);
                                    $state.reload();
                                    //refresh();
                                    //$state.go('messages');
                                    $scope.searchMessages = false;
                                }
                            });
                        }
                    };

                    function refresh() { //Refresh request
                        var tmp = {};
                        var tmpChat = {};
                        var tmpContract = {};
                        $scope.chats = [];
                        $scope.private = [];
                        $scope.msgsContract = [];
                       // console.log($scope.messages);
                        for (var i = 0; i < $scope.messages.length; i++) {

                        	if($scope.messages[i].contractID !== null){

                                tmpContract[$scope.messages[i].receiversNicks] =$scope.messages[i];
                            }else if($scope.messages[i].chatGroup){
                                console.log($scope.messages[i]);
                                tmpChat[$scope.messages[i].receiversNicks] =$scope.messages[i];
                            }else{
                                tmp[$scope.messages[i].receiverName] = $scope.messages[i].receiverId;
                            }
                        }
                        for (var j in tmp) {
                            $scope.private.push({name: j, id: tmp[j]});
                        }
                        for (var j in tmpChat) {
                            $scope.chats.push({name: j, id: tmpChat[j]});
                        }
                        for (var j in tmpContract) {
                            $scope.msgsContract.push({name: j, id: tmpContract[j]});
                        }
                        console.log($scope.msgsContract);

                    }

                    function loadMessages() {
                        $scope.messagesPrivate = [];
                        $scope.messagesChats = [];
                        $scope.messagesContract = [];
                        $scope.messages = [];
                       // $scope.chats = [];
                        $scope.searchMessages = true;

                        if ($scope.stream != null) {
                            $scope.stream.abort();
                        }
                        Oboe(
                            {
                                url: RESTAPISERVER + "/api/messages/",
                                pattern: '!',
                                withCredentials: true,
                                headers: {'Auth-Token': $http.defaults.headers.common['Auth-Token']},
                                start: function (stream) {
                                    // handle to the stream
                                    $scope.stream = stream;
                                    $scope.status = 'started';
                                    $scope.searchMessages = true;
                                },
                                done: function (parsedJSON) {
                                    $scope.status = 'done';
                                    $scope.searchMessages = false;
                                }
                            }).then(function () {
                        }, function (error) {
                        }, function (node) {
                            if (node != null && node.length != 0) {
                                for (var i = 0; i < node.length; i++) {

                                    if(node[i].contractID !== null){
                                    	$scope.messagesContract.push(node[i]);
                                    }else if(node[i].chatGroup){
                                        $scope.messagesChats.push(node[i]);
                                    }else{
                                        $scope.messagesPrivate.push(node[i]);
                                    }
                                    $scope.messages.push(node[i]);
                                }

                            }
                            refresh();
                        });
                    }
                }
            })
            .state('addMessage', {
                url: '/messages/new',
                templateUrl: 'newMessage.html',
                controller: function ($rootScope, $scope, $state, $stateParams, Message, User, $http, Oboe) {
                    isUserConnected($http, $rootScope, $scope, $state, User);
                    $scope.findAll = function(query){
                        return $http.get(RESTAPISERVER+'/api/users/');
                    }
                    $scope.app.configHeader({contextButton: '', title: 'New message', back: 'yes'});
                    $scope.action = 'add'; //Specify to the template we are adding a message, since it the same template as the one for editing.

                    $scope.results = []; //The currently received and displaid results
                    $scope.stream = null; //The stream of async results

                    $scope.pushResult = function ($obj) { //to add an object in the result list
                        if ($obj == null) return; //check if valid
                        for (var i = 0; i < $scope.results.length; i++) { //check if not already there
                            if ($scope.results[i].id == $obj.id) return;
                        }
                        $scope.results.push($obj); //OK, add
                    }

                    //User is available thanks to restApi.js
                    var currentUser = User.get({
                        id: $scope.app.userid
                    });
                    $scope.submit = function () {
                        if (true) {
                            $scope.errorUsername = false;
                            $scope.errorFields = false;
                            $scope.sendMessage = true;
                            var ids = [];
                            var nicks = [];
                            angular.forEach($scope.tags,function(value,key){
                                angular.forEach(value, function(value2,key2){
                                    if(key2==="id"){
                                        ids.push(value2);
                                    }
                                    if(key2==="nick"){
                                        nicks.push(value2);
                                    }
                                });
                            });
                            ids.push($scope.app.userid);
                            nicks.push(currentUser.nick);
                            var isChatGroup = ids.length>2;
                            var message = new Message({
                                receivers: ids,
                                receiversNicks: nicks,
                                messageContent: $scope.messageContent,
                                chatGroup: isChatGroup
                            });

                            Oboe({
                                url: RESTAPISERVER + "/api/messages/",
                                method: 'POST',
                                body: message,
                                withCredentials: true,
                                headers: {'Auth-Token': $http.defaults.headers.common['Auth-Token']},
                                start: function (stream) {
                                    // handle to the stream
                                    $scope.stream = stream;
                                    $scope.status = 'started';
                                    $scope.sendMessage = true;
                                },
                                done: function (parsedJSON) {
                                    $scope.status = 'done';
                                    $scope.sendMessage = false;
                                }
                            }).then(function () {
                            }, function (error) {
                                $scope.sendMessage = false;
                            }, function (node) {
                                if (node != null && node.length != 0) {
                                    $scope.sendMessage = false;
                                    console.log(node);
                                    $state.go('messages');
                                }
                            });
                        }
                        else {
                            $scope.errorFields = true;
                        }
                    };



                    $scope.selectUser = function ($stateParams) {
                        $scope.hideAfterSelected = false;
                        $scope.receiverId = $stateParams.receiver.id;
                        $scope.receiverPbkey = $stateParams.receiver.key.publicKey;
                    };


                }
            });
    });

	module.controller('display', function($scope){
		//user : person we are talking to

		var currentUser=0;

		//TODO: Make this automatic with a connexion to the server
		//Show 		: tells us what conversation to print on screen
		//id 		: useful to toggle the view
		//activity 	: "active" for the toggled viem, "" otherwise
		//mail 		: an array of messages ordered by date decreasingly
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
