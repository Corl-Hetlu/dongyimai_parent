app.controller("searchController",function ($scope,$location,searchService) {


    $scope.search=function () {
        $scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
        searchService.search( $scope.searchMap).success(function (response) {
            $scope.resultMap=response;//搜索返回的结果
            buildPageLabel();//调用
        });
    }

    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};//搜索对象
    $scope.addSearchItem=function(key,value){
        $scope.searchMap.pageNo=1;
        if(key =='brand' ||key == 'category'||key=='price'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key]=value;
        }

        $scope.search();
    }

    $scope.removeSearchItem=function(key){
        $scope.searchMap.pageNo=1;
        if(key=="category" ||  key=="brand" ||key=='price'){//如果是分类或品牌
            $scope.searchMap[key]="";
        }else{//否则是规格
            delete $scope.searchMap.spec[key];//移除此属性
        }
        $scope.search();
    }
    //分页方法
    buildPageLabel=function () {
        $scope.pageLabel=[];//新增分页栏属性
        var maxPageNo= $scope.resultMap.totalPages;//得到最后页码
        var firstPage=1;//开始页码
        var lastPage=maxPageNo;//截止页码
        $scope.firstDot=false;//前面有点
        $scope.lastDot=false;//后边有点
        if ($scope.resultMap.totalPages>5){
            if ($scope.searchMap.pageNo<=3){
                lastPage=5;
                $scope.firstDot=false;
            }else if($scope.searchMap.pageNo>=maxPageNo-2){
                firstPage=maxPageNo-4;
                $scope.lastDot=false;
            }else{
                firstPage=$scope.searchMap.pageNo-2;
                lastPage=$scope.searchMap.pageNo+2;
                $scope.firstDot=true;
                $scope.lastDot=true;
            }
           //循环产生页码标签
        }
        for(var i = 1;i<=lastPage;i++){
            $scope.pageLabel.push(i);
        }

    }

    $scope.queryByPage=function(pageNo){
        if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
            return;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
    }

    //判断当前页为第一页
    $scope.isTopPage=function(){
        if($scope.searchMap.pageNo==1){
            return true;
        }else{
            return false;
        }
    }
//判断指定页码是否是当前页
    $scope.ispage=function (p) {
        if(parseInt(p)==parseInt($scope.searchMap.pageNo)){
            return true;
        }else {
            return false;
        }
    }

    $scope.sortSearch=function(sortField,sort){
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();
    }

    //判断关键字是不是品牌
    $scope.keywordsIsBrand=function(){
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){//如果包含
                return true;
            }
        }
        return false;
    }
    $scope.loadkeywords=function(){
        $scope.searchMap.keywords=  $location.search()['keywords'];
        $scope.search();
    }
});