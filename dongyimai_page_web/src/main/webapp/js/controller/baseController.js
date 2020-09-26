app.controller('baseController',function ($scope) {

    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 5,
        perPageOptions: [5, 10, 15, 20, 25],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };
    $scope.selectIds=[];//选中的ID集合
    $scope.updateSelection=function ($event,id) {
        if ($event.target.checked){
            $scope.selectIds.push(id);
        }else{
            var index=$scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    }
    $scope.reloadList=function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }

    $scope.jsonToString=function(jsonString,key){
        var json=JSON.parse(jsonString);//将json字符串转换为json对象
        var value="";
        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=","
            }
            value+=json[i][key];
        }
        return value;
    }
});