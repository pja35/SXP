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
function updateActionExchange($scope,d){
	console.log("Update Existe Exchange "+Exchange);
	return true;
}


/****** Functions to handle the modification of an implementing or termination modality ******/
function modifyImpModality($scope, m){
	$scope.form.addImpModality = m;
	$scope.modifImpMod.toModify = true;
	$scope.modifImpMod.index = $scope.impModalities.indexOf(m);
}
function modifyTermModality($scope, m){
	$scope.form.addTermModality = m;
	$scope.modifTermMod.toModify = true;
	$scope.modifTermMod.index = $scope.termModalities.indexOf(m);
}
function cancelImpModality($scope){
  endModifImpMod(false);
}
function cancelTermModality($scope){
  endModifTermMod(false);
}
function validateImpModality($scope){
	endModifImpMod(true);
}
function validateTermModality($scope){
	endModifTermMod(true);
}
function endModifImpMod ($scope, toValidate){
	if (toValidate == true)
	{
		var mod = $scope.form.addImpModality;
		var index = $scope.modifImpMod.index;
		if (index != -1)
		{
			$scope.impModalities[index] = mod;
		}
	}
	$scope.form.addImpModality = "";
	$scope.modifImpMod.toModify = false;
	$scope.modifImpMod.index = -1;
}
function endModifTermMod ($scope, toValidate){
	if (toValidate == true)
	{
		var mod = $scope.form.addTermModality;
		var index = $scope.modifTermMod.index;
		if (index != -1)
		{
			$scope.termModalities[index] = mod;
		}
	}
	$scope.form.addTermModality = "";
	$scope.modifTermMod.toModify = false;
	$scope.modifTermMod.index = -1;
}
/*****************************************************************************/

/****** Functions to handle the deleting of a clause : party, exchange, implementing modality, termination modality ******/
function deleteImpModality($scope, m){
	var index = $scope.impModalities.indexOf(m);
	if (index > -1){
		$scope.impModalities.splice(index, 1);
	}
}
function deleteTermModality($scope, c){
	var index = $scope.termModalities.indexOf(c);
	if (index > -1){
		$scope.termModalities.splice(index, 1);
	}
}
function deleteExchange($scope, e){
	var index = $scope.exchangeClause.indexOf(e);
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

/****** Functions to handle the adding of a clause : party, exchange, implementing modality, termination modality ******/
function updateParties($scope){
	var addParty = $scope.form.addParty.split(" - ");
  var newParty = {};
  newParty.key = addParty[1];
  newParty.value = addParty[0];
  var index = $scope.parties.findIndex(party => party.key === newParty.key);
	if (newParty.key != undefined && index == -1){
    $scope.parties.push(newParty);
		$scope.form.addParty = "";
	}
}

function updateTermModalities($scope){
	var mod = $scope.form.addTermModality;
	var index = $scope.termModalities.indexOf(mod);
	if (index == -1){
		$scope.termModalities.push(mod);
		$scope.form.addTermModality = "";
	}
}

function updateImpModality($scope){
	var mod = $scope.form.addImpModality;
	var index = $scope.impModalities.indexOf(mod);
	if (index == -1){
		$scope.impModalities.push(mod);
		$scope.form.addImpModality = "";
	}
}

function updateExchanges($scope){

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
	return false;
}
/*****************************************************************************/

/****** Function to get all the users from the database ******/
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
/****** Function to get all the items from the database ******/
function getItems($http, $scope, currentFromUser){
	$http.get(RESTAPISERVER + "/api/items/all").then(
		function(response){
			var allItems = response.data;
			$scope.items = [];
			for(i = 0; i < allItems.length; i++){
				if (allItems[i].nick != "" & allItems[i].userid == currentFromUser){
					$scope.items.push(allItems[i].title);
				}
			}
		}
	);
}

/****** Function to check whether the user fill out all the mandatory information about the contract ******/
function checkClauses($scope){

	var isOK = true;

	// Contract name
	if ($scope.form.title == null)
	{
		$scope.errorName = true;
		isOK = false;
	}
	else
	{
		$scope.errorName = false;
	}
	// Parties
	if ($scope.parties.length == 0)
	{
		$scope.errorParty = true;
		isOK = false;
	}
	else
	{
		$scope.errorParty = false;
	}
	// Exchanges
	if ($scope.Exchange.length == 0)
	{
		$scope.errorExchange = true;
		isOK = false;
	}
	else
	{
		$scope.errorExchange = false;
	}
	// Implementing modalities
	if ($scope.impModalities.length == 0)
	{
		$scope.errorImpModality = true;
		isOK = false;
	}
	else
	{
		$scope.errorImpModality = false;
	}
	// Termination modalities
	if ($scope.termModalities.length == 0)
	{
		$scope.errorTermModality = true;
		isOK = false;
	}
	else
	{
		$scope.errorTermModality = false;
	}

	return isOK;
}
/************************************************************/
