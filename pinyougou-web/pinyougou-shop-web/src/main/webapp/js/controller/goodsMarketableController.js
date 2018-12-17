/** 定义控制器层 */
app.controller('goodsMarketableController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});

    /** 定义商品状态数组 */
    $scope.status = ['未审核', '已审核', '审核未通过', '关闭'];

    /** 分页查询 */
    $scope.search = function (page, rows) {
        baseService.findByPage("/goodsMarketable/findByPage", page,
            rows)
            .then(function (response) {
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };


    /** 商家商品上下架(修改可销售状态) */
    $scope.updateMarketable = function (status) {
        if ($scope.ids.length > 0) {
            if (status == '0') {
                if (!confirm("你确认要下架吗？")) {
                    return;
                }
            }
            baseService.sendGet("/goodsMarketable/updateMarketable?ids=" + $scope.ids + "&status=" + status)
                .then(function (response) {
                if (response.data) {
                    /** 清空ids数组 */
                    $scope.ids = [];
                    /** 重新加载数据 */
                    $scope.reload();
                } else {
                    alert("操作失败！");
                }
            });
        } else {
            alert("请选择要操作的商品！");
        }
    }
});