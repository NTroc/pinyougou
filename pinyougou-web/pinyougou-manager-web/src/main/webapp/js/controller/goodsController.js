/** 定义控制器层 */
app.controller('goodsController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    /** 定义商品状态数组 */
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];

    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {
        baseService.findByPage("/goods/findByPage", page,
            rows, $scope.searchEntity)
            .then(function (response) {
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 审批商品，修改状态 */
    $scope.updateStatus = function (status) {
        if ($scope.ids.length > 0) {
            /** 调用服务层 */
            baseService.sendGet("/goods/updateStatus?ids=" + $scope.ids + "&status=" + status)
                .then(function (response) {
                    if (response.data) {// 成功
                        /** 重新加载数据 */
                        $scope.reload();
                        /** 清空ids数组 */
                        $scope.ids = [];
                    } else {
                        alert("操作失败！");
                    }
                });
        } else {
            alert("请选择要审核的商品！");
        }
    };


    /** 批量删除 (逻辑删除)*/
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/goods/delete", $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        /** 重新加载数据 */
                        $scope.reload();
                    } else {
                        alert("删除失败！");
                    }
                });
        } else {
            alert("请选择要删除的记录！");
        }
    };
});