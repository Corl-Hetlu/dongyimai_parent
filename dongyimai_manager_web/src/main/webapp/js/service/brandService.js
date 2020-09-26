app.service('brandService',function ($http) {

    this.save=function (entity) {
        var method="add";
        if(entity.id!=null){
            method="update";
        }
        return $http.post("../brand/"+method+".do",entity);
    }
    this.findOne=function (id) {
        return $http.get("../brand/findOne.do?id="+id);
    }

    this.dele=function (selectIds) {
        $http.get("../brand/delete.do?ids=" + selectIds);
    }

    this.findBrandList=function () {
        return $http.get('../brand/selectOptionList.do');
    }

    this.findPage=function (page,rows) {
        return $http.post("../brand/findPage.do?page="+page+"&rows="+rows);
    }
    this.search=function (page,rows,searchEntity) {
        return $http.post("../brand/search.do?page="+page+"&rows="+rows,searchEntity);
    }
})