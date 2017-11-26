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
                    $scope.privateIsClicked = true;

                    // function to know witch tabs currently clicked in messages
                    $scope.showMessage = function(){
                        $scope.privateIsClicked = true;
                    }
                    $scope.showForm = function(){
                        $scope.privateIsClicked = false;
                    }


                    /**
                     * function to open a specific message
                     * when click on message in tabs private message
                     * @param messageName the id of message that we have to display
                     * @param tabcontent the id of content that we have to hide
                     */
                    $scope.openMessage = function (messageName,tabcontent) {
                        document.getElementById(tabcontent).style.display = "none";
                        document.getElementById(messageName).style.display = "block";
                    }
                    /**
                     * when we click to open message in tabs Forum/contract
                     *
                     * @param tabName the id of tab that we have to display
                     * @param tabcontent the id of tab that we have to hide
                     */
                    $scope.back = function(tabName,tabcontent){
                        document.getElementById(tabName).style.display = "block";
                        document.getElementById(tabcontent).style.display = "none";
                    }

                    $scope.stream = null; //The stream of async results

                    // get the current user with current id
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
                                receivers: chatId.details.receivers,
                                receiversNicks: chatId.details.receiversNicks,
                                messageContent: messageContent,
                                contractTitle: chatId.details.contractTitle,
                                contractID: chatId.details.idC,
                                chatID: chatId.details.chatID
                            });
                           /* console.log("add message");
                            console.log(message);*/
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
                        var tmpContract = {};




                        $scope.chats = [];
                        $scope.private = [];
                        $scope.msgsContract = [];
                       // console.log($scope.messages);
                        for (var i = 0; i < $scope.messages.length; i++) {
                            console.log($scope.messages[i]);
                        	if($scope.messages[i].contractID != null){
                               // console.log($scope.messages[i]);

                                var detailsContract = {};
                                detailsContract['date'] = $scope.messages[i].sendingDate;
                                detailsContract['idC'] = $scope.messages[i].contractID;
                                detailsContract['id'] = $scope.messages[i].chatID;
                                detailsContract['content'] = $scope.messages[i].messageContent;
                                detailsContract['receivers']= $scope.messages[i].receivers;
                                detailsContract['receiversNicks'] = $scope.messages[i].receiversNicks;
                                detailsContract['contractTitle'] = $scope.messages[i].contractTitle;
                                tmpContract[$scope.messages[i].contractID] = detailsContract;


                            }else{
                                var detailsPrivate = {};
                                detailsPrivate['date'] = $scope.messages[i].sendingDate;
                                detailsPrivate['content'] = $scope.messages[i].messageContent;
                                detailsPrivate['id'] = $scope.messages[i].id;
                                detailsPrivate['chatID'] = $scope.messages[i].chatID;
                                detailsPrivate['receivers']= $scope.messages[i].receivers;
                                detailsPrivate['receiversNicks'] = $scope.messages[i].receiversNicks;
                              //  console.log($scope.messages[i]);
                                //if($scope.messages[i].sender)
                                tmp[$scope.messages[i].receiverName] = detailsPrivate;



                            }
                        }
                        for (var j in tmp) {
                            $scope.private.push({name: j, details: tmp[j]});
                        }
                        for (var j in tmpContract) {
                            $scope.msgsContract.push({name: j, details: tmpContract[j]});
                        }
                        console.log($scope.private);

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
                            console.log("SCOPE");
                            console.log($scope.messageContent);
                            var message = new Message({
                                receivers: ids,
                                receiversNicks: nicks,
                                messageContent: $scope.messageContent

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
