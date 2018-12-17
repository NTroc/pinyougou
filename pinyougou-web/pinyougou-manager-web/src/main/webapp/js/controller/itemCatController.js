/** 定义控制器层 */
app.controller('itemCatController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    /** 定义变量记录父级id */
    $scope.parentId = 0;

    /** 根据上级ID显示下级列表 */
    $scope.findItemCatByParentId = function (parentId) {
        /** 设置父级id */
        $scope.parentId = parentId;
        baseService.sendGet("/itemCat/findItemCatByParentId", "parentId=" + parentId).then(function (response) {
            $scope.dataList = response.data;
        })
    }

    /*定义级别变量，默认为1级*/
    $scope.grade = 1;
    /*查询下级*/
    $scope.selectList = function (entity, grade) {
        $scope.grade = grade;
        if (grade == 2) {
            //把一级分类放入$scope
            $scope.itemCat1 = entity;
        }
        if (grade == 3) {
            //把二级分类放入$scope
            $scope.itemCat2 = entity;
        }

        /** 查询此级下级列表 */
        $scope.findItemCatByParentId(entity.id);
    }

    /*查询商品类型模板typeTemplateList*/
    $scope.findTypeTemplateList = function () {
        baseService.sendGet("/typeTemplate/findTypeTemplateList").then(function (response) {
            $scope.typeTemplateList = response.data;
        })
    }

    /** 添加或修改 */
    $scope.saveOrUpdate = function () {
        var url = "save";
        if ($scope.entity.id) {
            url = "update";
        }else{
            /** 添加时设置父级id */
            $scope.entity.parentId = $scope.parentId;
        }
        /** 发送post请求 */
        baseService.sendPost("/itemCat/" + url, $scope.entity)
            .then(function (response) {
                if (response.data) {
                    /** 重新加载数据 */
                    $scope.findItemCatByParentId($scope.parentId);
                    $scope.entity = null;
                } else {
                    alert("操作失败！");
                }
            });
    };

    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 批量删除 */
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/itemCat/delete", $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        /** 重新加载数据 */
                        $scope.findItemCatByParentId($scope.parentId);
                    } else {
                        alert("删除失败！");
                    }
                });
        } else {
            alert("请选择要删除的记录！");
        }
    };
});