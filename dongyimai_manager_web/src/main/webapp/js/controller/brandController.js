app.controller('brandController',function ($scope,$controller,brandService) {
    $controller("baseController",{$scope:$scope});

    $scope.search=function (page,rows) {
        brandService.search(page,rows,$scope.searchEntity).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;
        })
    }

    
    $scope.searchEntity = {};
    $scope.findPage=function (page,rows) {
        brandService.findPage(page,rows).success(function (response) {
            $scope.list=response.rows;
            $scope.paginationConf.totalItems=response.total;
        })
    }

    $scope.save=function (entity) {
        brandService.save(entity).success(function (response) {
            if(response.success){
                $scope.reloadList();
            }else{
                alert(response.message);
            }
        })
    }
    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity=response;
        })
    }
    $scope.dele=function () {
        brandService.dele($scope.selectIds).success(function (response) {
            if(response.success){
                $scope.reloadList();
                $scope.selectIds = [];
            }else{
                alert(response.message);
            }
        })
    }
});