(function() {
    var module = angular.module('app.contracts', []);
    module.config(function($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise("/"); //if someone messes up the url, go back home
        $stateProvider
	        .state('viewContracts', {
	            url: '/contracts',
	            templateUrl: 'contracts/contracts.html',
	            controller: 'viewContracts'
	        })
	        .state('viewContract', {
	            url: '/contracts/view/:id',
	            templateUrl: 'contracts/contract.html',
	            controller: 'viewContract'
	        })
		    .state('editContract', {
		        url: '/contracts/edit/:id',
		        templateUrl: 'contracts/contract-form.html',
		        controller: 'editContract'
		    })
		    .state('addContract', {
		        url: '/contracts/add',
		        templateUrl: 'contracts/contract-form.html',
		        controller: 'addContract'
		    });
    });


    // 'contracts' state controller function
    module.controller('viewContracts', function($scope, Contract) {
    	//TODO: do some of this with configHeader:
    	$scope.app.configHeader({back: false, title: 'Contracts', contextButton: 'addContract'});

    	$scope.contracts = [];
    	$scope.contracts = Contract.query(); //Fetch contracts, thanks to restApi.js
    	//The bindings with contracts.html will display them automatically
    });



    // 'View contract' state controller function
    module.controller('viewContract',  function($scope,$window, $http, $stateParams, Contract, $state) {
	  $scope.app.configHeader({back: true, title: 'View contract', contextButton: 'editContract', contextId: $stateParams.id});

	  var contract = Contract.get({id: $stateParams.id}, function() {
	    //Just load the contract and display it via the bindings with contract.html
	    $scope.contract = contract;

	// this variable help to get all information about contract
	    $scope.title = contract.title
    	$scope.clauses = contract.clauses;
	    $scope.canceled = contract.canceled;
	    $scope.modality = contract.modality;
	    $scope.exchangeClause=contract.exchange;


        /*
        * Actually Exchange clauss is Array<String> content all information about this exchange with string
        */
        $scope.Exchange=[];
        ex=contract.exchange;

          for (i=0; i<ex.length; i++){
          exchange=ex[i];
          console.log("Ex="+exchange);
          $scope.myarrays=[];
          $scope.myarrays=exchange.split('*');
          $scope.Exchange.push({'from':$scope.myarrays[0],'to':$scope.myarrays[1],'item':$scope.myarrays[2],'when':$scope.myarrays[3],'how':$scope.myarrays[4],'details':$scope.myarrays[5]});
          }



	    // Get parties from the hashmap of names and id (to identify exactly a user)
	    $scope.parties = [];
	    $scope.nameParties=[];
	    $scope.body=[];
	    pN = contract.partiesNames;


	    for (i=0; i<pN.length; i++){
	    	names = pN[i];

	    	$scope.body[i]=[{ text:"Parti "+(i+1),style:'title'},names["value"]];
	    	console.log(names["value"]);
	    	$scope.parties[i] = names["value"] + " - " + names["key"];
	    }
	  });

	  $scope.pdfMake = $window.pdfMake;

	  $scope.modify = function(){
		  $state.go("editContract", {id : contract.id});
	  };

	  $scope.sign = function(){
		  $http.put(RESTAPISERVER + '/api/contracts/sign/:id', contract.id);
		  $state.go('viewContracts');
	  }

	  $scope.decline = function(){
		  $http.put(RESTAPISERVER + '/api/contracts/cancel/:id', contract.id);
		  $state.go('viewContracts');
	  }


// fonction to generate Pdf about one Contrat
	  $scope.getPdf = function(){

	  console.log($scope.body);
      var pdfMake = $scope.pdfMake;

      var teste=$scope.test;
      var docDefinition = {
                                  content: [
                                    { text: "Contrat d'echange", style: 'header' },
                                    {text:"1 . PREAMBULE :",style:'title'},
                                   "Le contrat d’échange des Objects est à but non lucratif. Il n’y a aucun échange d’argent entre les parties, chacune de celles-ci cédant à l’autre son Object définit ci-dessous à titre gratuit et définitivement." ,
                                     {text:" 2 . CONTRAT D’ECHANGE ENTRE :",style:'title'},
                                     $scope.body,
                                  ],

                                  styles: {
                                 	header: {
                                 			fontSize: 18,
                                 			bold: true,
                                 			margin: [200, 0, 0, 50]
                                 		},
                                 		title: {
                                           fontSize: 14,
                                           bold: true,
                                           margin: [10, 20, 0, 20]
                                                 },
                                  }
                                };

           //"pdfMake" create document pdf and opened
           pdfMake.createPdf(docDefinition).open();
            //   pdfMake.createPdf(docDefinition).download('optionalName.pdf');



      	  }

	});


    module.controller('editContract', function($scope, $stateParams, Contract, $state, $http){

      	$scope.app.configHeader({back: true, title: 'Edit contracts', contextId: $stateParams.id});
      	$scope.action = 'edit';

  		  $scope.form = {};
      	$scope.userList = [];
        getUsers($http, $scope);
        $scope.parties = [];
				$scope.clauseExists = true;

  		  var contract = Contract.get({id: $stateParams.id}, function() {
  			//First, load the item and display it via the bindings with item-form.html
  			$scope.form.title = contract.title;
  			$scope.clauses = contract.clauses;
        $scope.parties = contract.partiesNames;
        $scope.impModalities = contract.impModalities; //faut créer le truc derriere en java
        });

				checkClauses($scope);


/******All Fonction to add or Upadate information about contrat*******/
      	$scope.updateparties = function() {updateParties($scope)};
      	$scope.updateclauses = function() {updateClauses($scope)};
        $scope.updateImpModalities = function() {updateImpModalities($scope)};
        $scope.updateTermModalities = function() {updateTermModalities($scope)};

      	$scope.deleteParty = function(p){deleteParty($scope,p);};
      	$scope.deleteClause = function(c){deleteClause($scope,c);};
        $scope.deleteImpModality = function(m) {deleteImpModality($scope, m)};
        $scope.deleteTermModality = function(m) {deleteTermModality($scope, m)};

/*******************************************************************/

      	$scope.submit = function() {

					var isOK = checkClauses($scope);
					if (isOK)
					{
	          partiesId = [];
	          $scope.parties.forEach(function(party) {
	            partiesId.push(party.key);
	          });

	      		if ($scope.form.addParty != null && $scope.form.addParty.length>2){updateParties($scope);}
	      		if ($scope.form.addClause != null && $scope.form.addClause.length>2){updateClauses($scope);}
	          if ($scope.form.addImpModality != null && $scope.form.addImpModality.length>2){updateImpModalities($scope);}
	          if ($scope.form.addTermModality != null && $scope.form.addTermModality.length>2){updateTermModalities($scope);}
	          //Contract is available thanks to restApi.js
	      		contract.title = $scope.form.title;
	      		contract.clauses = $scope.clauses;
	      		contract.parties = partiesId;

	      		contract.$update(function() {
	      			$state.go('viewContracts');
	      	  });
					}
      	};

      	$scope.delete = function(){
      		contract.$delete(function(){
      			 $state.go('viewContracts');
      		})
      	};


    });



    module.controller('addContract', function($rootScope, $scope, Contract, Item, $state, $http){

        //this function manages the disconnection because if the session expresses the return to the connection page
       isUserConnected($rootScope, $scope, $state);

        //Add Title
    	$scope.app.configHeader({back: true, title: 'Add contracts'});


    	$scope.action = 'add';


/***Initilize all variable we need to manages contract*******/

    	$scope.parties=[];
    	$scope.clauses=[];
    	$scope.canceled=[];
    	$scope.Exchange=[];
        $scope.Party=[];
    	$scope.modality=[];
    	$scope.exchangeClause=[];
        $scope.userList =[];
    	$scope.items = [];
		$scope.items = Item.query();


/***********************************************/

		$scope.exchangeModes = [];
			var itemsY = [];
			//itemsY = Item.query();
			console.log("Nombre d'items");
			console.log($scope.items.length);
			/*********************************/
      $scope.upCanCeled=false;// variable servant a affichez le bouton modify
      $scope.upModality=false;// si Variable==false bouton Add est visible sion le bouton Modift
      $scope.upExchange=false;

      /***********************************/
      /* These Next variable allows us to recover the index
      *  of Modality when wants change
      */
      $scope.IndexModality=-1;
      $scope.IndexCanceled=-1;

      /***********************************/

     	$scope.ModUpText="";
     	getUsers($http, $scope);

			$scope.exchangeModes[0] = "electronically";
			$scope.exchangeModes[1] = "delivery";
			$scope.exchangeModes[2] = "in person";

      /*Definition default  Modality*/
      var Modality1= "Les parties s'engagent à préciser les détériorations et/ou modifications de l'objet depuis la signature du contrat 4 jours avant l'échange.";
      var Modality2="A effectuer ensemble une évaluation  des  objectes à l’échange.";
      var Modality3="Les parties reconnaissent avoir pris connaissance de toutes informations concernant les Objects "+
                    " échangé et ne pourront se retourner contre l’ancien propriétaire en cas de problèmes et/ou de "+
                    " dysfonctionnement futurs. ";
      var Modality4="A mettre à disposition un classeur contenant les documents utiles (manuels d’utilisation de "+
                     "l’électronique, des moteurs et de tout autre appareillage ou système requérant un mode "+
                     "d’utilisation spécifique  ";

      var Modality5="A mettre à disposition un Objet propre et dans  état indique dans la description conforme à la réglementation de "+
                     "sécurité. ";

      var Modality6="Apporte une pieces d'identite lors d'echanges";

      var canceled1="Ce Contrat ne sera pas aboutit si la description  l'object n'est pas conforme à ce qu'il est décrit dans sa descriptions";



       /************Add these default to scope modality***********/
		$scope.modality.push(Modality5);
		$scope.modality.push(Modality4);
	    $scope.modality.push(Modality3);
	    $scope.modality.push(Modality2);
	    $scope.modality.push(Modality1);

       /********************************************************/

	    /************Add default clausse Termination **/
	    $scope.canceled.push(canceled1);
        /********************************************/


        /************************there fonction Menage to adding **************************************/
    	$scope.updateparties = function() {updateParties($scope)};
    	$scope.updateclauses= function() {updateClauses($scope)};
    	$scope.updateclausescanceled= function() {updateClausesCanceled($scope)};
    	$scope.updatemodality= function() {updateModality($scope)};
    	$scope.updateclausesExchange= function() {updateClausesExchange($scope)};

        /***********************there Menage to Update ***********************************************/
        $scope.updatecanceledExist= function() {updatecanceledExist($scope)};
    	$scope.updatemodalityExist= function() {updatemodalityExist($scope)};
    	$scope.updateclausesExchangeExist= function() {updateClausesExchange($scope)};

    	/***************There fonction menage front-end when user click Update*****************************************************************************/

    	      $scope.updateActionModality=function(m){updateActionModality($scope,m)};
              $scope.updateActionCanceled=function(c){updateActionCanceled($scope,c)};
              $scope.updateActionEx=function(e){updateActionExchange($scope,e)};
              $scope.update=function(){
                                            	if($scope.fromexchange!="")
                                            	{

                                            	$scope.tap=$scope.fromExchange.split(" - ");
                                            	$scope.actueluser=$scope.tap[1];
                                              	getItems($http, $scope);
                                            	}
                                      };
        /********************************************************************************************/


        /**************Menage Canceled when to want update contract Modality or ***************************/

        $scope.AnnulerCanceled = function() {AnnulerCanceled($scope)};
        $scope.AnnulerModality = function() {AnnulerModality($scope)}

        /****************************************/
    	$scope.deleteParty = function(p){deleteParty($scope,p);};
    	$scope.deleteModality = function(m){deleteModality($scope,m);};
    	$scope.deleteCanceled = function(c){deleteCanceled($scope,c);};
        $scope.deleteExchange = function(e){deleteExchange($scope,e);};
    	$scope.deleteClause = function(c){deleteClause($scope,c);};













      var newParty = {};
      newParty.key = addParty[1];
      newParty.value = addParty[0];



    	$scope.submit = function() {

// variable isOk is boolean variable , this become true where user has entered all the mandatory information of a contract else become false */
				var isOK = checkClauses($scope);
				console.log("IsOK="+isOK)
				if (isOK)
				{
	        	pN = $scope.parties;
	            partiesId = [];
	    	  for (i=0; i<pN.length; i++){
	    	   	names = pN[i];
	    	   	partiesId[i] = names.split(" - ")[1];
	    	  };

	    		if ($scope.form.addParty != null && $scope.form.addParty.length>2){updateParties($scope);}
	    		if ($scope.form.addClause != null && $scope.form.addClause.length>2){updateClauses($scope);}
	    		if ($scope.form.addCanceled != null && $scope.form.addCanceled.length>2){updateClausesCanceled($scope);}
	        if ($scope.form.addModClause != null && $scope.form.addModClause.length>2){updateModality($scope);}
	    		if ($scope.form.addExchangeClause!= null && $scope.form.addExchangeClause.length>2){updateClausesExchange($scope);}

	    		var contract = new Contract({
		    		title : $scope.form.title,
	    			clauses: $scope.clauses,
	    			canceled:$scope.canceled,
	    			modality:$scope.modality,
	    			exchange:$scope.exchangeClause,
		    		parties: partiesId
					});

	      	console.log("Contrat=="+contract.exchange);

	      	// Create the contract in the database thanks to restApi.js
	    		contract.$save(function() {
	    			console.log("ContratSav=="+contract.exchange+"/"+contract.canceled);
						$state.go('viewContracts');
					});
				}
    	};
    });


  //directives define new html tags or attributes; think of them as macros.
  module.directive('contract', function() {
    return {
      restrict: 'E',
      templateUrl: 'contracts/one-contract.html' //TODO: rename this to item-one
    };
  });
  module.directive('party', function() {
    return {
      restrict: 'E',
      templateUrl: 'contracts/party.html'
    };
  });
  module.directive('clause', function() {
	    return {
	      restrict: 'E',
	      templateUrl: 'contracts/clause.html'
	    };
	  });

})();
function buildTableBody(data, columns) {
    var body = [];

    body.push(columns);

    data.forEach(function(row) {
        var dataRow = [];

        columns.forEach(function(column) {
            dataRow.push(row[column].toString());
        })

        body.push(dataRow);
    });

    return body;
}
/*******************there fonction menage the button Cancaled or Update on Front-End **********************/
function AnnulerCanceled($scope){

   $scope.form.addCanceled="";

// this boolean variable to manage visibily ,button  Update or Add the clausse Canceled
   $scope.upCanCeled=false;


   }
 function AnnulerModality($scope){

    $scope.form.addModClause="";
    // this boolean variable to manage visibily ,button  Update or Add
    $scope.upModality=false;


    }
function updateActionModality($scope,m){

$scope.form={};
$scope.form.addModClause=m;
var index=$scope.modality.indexOf(m);



$scope.upModality=true;

$scope.IndexModality=index;
	return false;
}
function updateActionCanceled($scope,c){

$scope.form={};
$scope.form.addCanceled=c;
var index=$scope.canceled.indexOf(c);
$scope.upCanCeled=true;
$scope.IndexCanceled=index;

return false;


}
function updateActionExchange($scope,d){

console.log("Update Existe Exchange "+Exchange);

	return true;
}
/********************************************************************/

 /*****************This variable menage to update information about clausse Modality,Termination*************/


function updatemodalityExist($scope){
console.log("Update Modality")
   var mod=$scope.form.addModClause;
	var index = $scope.IndexModality;
   if(index!=-1)
   {
   $scope.modality[index]=mod;
   $scope.form.addModClause="";
   $scope.upModality=false;
   }

   }

function updatecanceledExist($scope){
console.log("Update Canceled")
var can=$scope.form.addCanceled;
var index = $scope.IndexCanceled;


   if(index!=-1)
   {
   $scope.canceled[index]=can;
   $scope.form.addCanceled="";
   $scope.upCanCeled=false;
   }




   }

/*****************************************************************************/

/*********************There Fonction to menage deleting the information about clausse Modality,Canceled,Exchange ******/
function deleteModality($scope, mod){
console.log("Delete Modalite");

	var index = $scope.modality.indexOf(mod);
	if (index > -1){
		$scope.modality.splice(index, 1);
	}
}

function deleteCanceled($scope, cance){
console.log("Delete Canceled");

	var index = $scope.canceled.indexOf(cance);
	if (index > -1){
		$scope.canceled.splice(index, 1);
	}
}
function deleteExchange($scope, exchange){
console.log("Delete Modalite");

	var index = $scope.modality.indexOf(exchange);
	if (index > -1){
		$scope.exchangeClause.splice(index, 1);
	}
}
function deleteParty($scope, p){
	var index = $scope.parties.indexOf(p);
	if (index > -1){
		$scope.parties.splice(index, 1);
	}
}
/*****************************************************************************/

/**************************there fontion Menage Adding All information when ****************************************************/
function updateParties($scope){
  var addParty = $scope.form.addParty;
	var index = $scope.parties.indexOf(addParty);
	if (index == -1){
       $scope.parties.push(addParty.name+" - "+addParty.id);
        $scope.Party.push(addParty);
        $scope.form.addParty="";
	}



    }

function updateClausesCanceled($scope){
	var Canceled=$scope.form.addCanceled;


	var index = $scope.canceled.indexOf(Canceled);
	if (index == -1){
		$scope.canceled.push(Canceled);
		$scope.form.addCanceled="";
	}
	return false;
}

function updateModality($scope){

var Modality=$scope.form.addModClause;

console.log("Res="+Modality);
	var index = $scope.modality.indexOf(Modality);
	if (index == -1){
		$scope.modality.push(Modality);
		$scope.form.addModClause="";
	}
	return false;
}


function updateClausesExchange($scope){

	var from=$scope.fromExchange;
	var to=$scope.toExchange;
	var what=$scope.whatExchange;
	var when=""+$scope.whenExchange+" ";
	var how = $scope.howExchange;
	var details = $scope.detailsExchange;
	var exchange=from+"*"+to+"*"+what+"*"+when+"*"+how+"*"+details;
	var index = $scope.exchangeClause.indexOf(exchange);
	if (index == -1){

        $scope.exchangeClause.push(exchange);
		$scope.fromExchange="";
		$scope.whatExchange="";
	    $scope.whenExchange="";
		$scope.howExchange="";
		$scope.detailsExchange="";
		$scope.form.addExchangeClause="";
        $scope.Exchange.push({'from':from,'to':to,'item':what,'when':when,'how':how,'details':details});

	}



console.log($scope.Exchange);
	return false;
}
/************************Gette All user about Data Base*********************************/

function getUsers($http, $scope){
	$http.get(RESTAPISERVER + "/api/users/").then(
		function(response){
			var userList = response.data;
			$scope.userList = [];

			for(i=0; i<userList.length; i++){
				if (userList[i].nick != ""){
					$scope.userList[i] = { 'name' : userList[i].nick
							, 'id' : userList[i].id };
				}
			}
		}
	);
}
/************************Gette All user about Data Base*********************************/
function getItems($http, $scope){
	$http.get(RESTAPISERVER + "/api/items/all").then(
		function(response){
			var ItemList = response.data;
			$scope.ItemListes = [];
			for(i=0; i<ItemList.length; i++){
				if (ItemList[i].nick != "" & ItemList[i].userid==$scope.actueluser ){


						$scope.ItemListes.push(ItemList[i].title);


				}
			}
		console.log("ItemNv="+$scope.ItemListes +" Taille="+$scope.ItemListes.length);
		}
	);
}





/****************this fonction verify user has entered all the mandatory information of a contract********************************************/
function checkClauses($scope){

	var isOK = true;

	if ($scope.form.title == null)
	{
		$scope.hasName = true;
		isOK = false;
	}
	else
	{
		$scope.hasName = false;

	}

	if ($scope.parties.length < 0)
	{
		$scope.hasParty = true;
				isOK = false;
	}
	else
	{
		$scope.hasParty = false;

	}

	if ($scope.Exchange.length < 0)
	{
		$scope.hasExchanges = true;
				isOK = false;
	}
	else
	{
		$scope.hasExchanges = false;

	}

	if ($scope.modality.length < 0)
	{
		$scope.hasImpModality = true;
				isOK = false;
	}
	else
	{
		$scope.hasImpModality = false;

	}

	if ($scope.canceled.length < 0)
	{
		$scope.hasTermModality = true;
				isOK = false;
	}
	else
	{
		$scope.hasTermModality = false;

	}

	return isOK;
}
/************************************************************/