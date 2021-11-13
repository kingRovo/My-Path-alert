jobPortalApp.controller('controllerInterestedJobs',function($scope, $http, $state, userSession) {


    console.log("in interested jobs");


    //get all interested jobs
    $http({
        method : "GET",
        url : '/jobseeker/'+ $state.params.profileDet.id
    }).success(function(data) {

        $scope.interestedJobs = data.interestedJobs;

    }).error(function(error) {
        console.log("Error in get jobseeker from view jobs");
        console.log(error);
    });


    $scope.toViewJob = function(requisitionId){
        $state.go("home.viewJob", {jobAndProfile: {profileDet: $state.params.profileDet, requisitionId: requisitionId}} );
    }




})