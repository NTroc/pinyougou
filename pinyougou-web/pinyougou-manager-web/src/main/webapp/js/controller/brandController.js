/** 定义基础的控制器 */
app.controller("brandController", function ($scope, $controller, baseService) {
    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});


    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {
        baseService.findByPage('/brand/findByPage', page, rows, $scope.searchEntity)
            .then(function (response) {
                // 获取响应数据 response.data {total : 100, rows : [{},{},{}]}
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            })
    };


    //添加或修改品牌
    $scope.saveOrUpdate = function () {
        //alert(JSON.stringify($scope.entity))
        //定义请求URL
        var url = "save"; // 添加品牌
        if ($scope.entity.id) {
            url = "update";// 修改品牌
        }
        //发送post请求
        baseService.sendPost("/brand/" + url, $scope.entity)
            .then(function (response) {
                //判断响应数据
                if (response.data) {
                    //重新查询数据
                    $scope.reload(); //重新加载
                } else {
                    alert("操作失败")
                }
            })
    };

    //为修改按钮绑定点击事件
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };

    /** 为删除按钮绑定点击事件 */
    $scope.delete = function () {
        if (confirm("确定删除选中项？")) {
            if ($scope.ids.length > 0) {
                /*发送异步请求*/
                baseService.sendGet("/brand/delete" + $scope.ids).then(function (response) {
                    //获取响应数据
                    if (response.data) {
                        //清空ids数组
                        $scope.ids = [];
                        /*重新加载品牌*/
                        $scope.reload();
                    }
                })
            } else {
                alert("请选择要删除的品牌！")
            }

        }
    }

});