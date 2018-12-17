/** 定义购物车控制器 */
app.controller('cartController', function ($scope, $controller, baseService) {
    /**  指定继承baseController*/
    $controller('baseController', {$scope: $scope});

    /** 查询购物车数据 */
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart").then(function (response) {
            $scope.carts = response.data;

            /** 定义总计对象 */
            $scope.totalEntity = {totalNum: 0, totalMoney: 0.00};
            for (var i = 0; i < response.data.length; i++) {
                // 获取购物车
                var cart = response.data[i];
                // 迭代购物车订单明细集合
                for (var j = 0; j < cart.orderItems.length; j++) {
                    // 获取订单明细
                    var orderItem = cart.orderItems[j];
                    // 购买总件数
                    $scope.totalEntity.totalNum += orderItem.num;
                    // 购买总金额
                    $scope.totalEntity.totalMoney += orderItem.totalFee;
                }
            }

        });
    };

    /** 添加SKU商品到购物车 */
    $scope.addCart = function (itemId, num) {
        baseService.sendGet("/cart/addCart?itemId=" + itemId + "&num=" + num).then(function (response) {
            if (response.data) {
                // 重新加载购物车数据
                $scope.findCart();
            } else {
                alert("操作失败！");
            }
        });
    }

    /** */
    $scope.updateNum = function (itemId, num) {
        var num = parseInt(num);
        if (num > 0) {
            baseService.sendGet("/cart/updateNum?itemId=" + itemId + "&num=" + num).then(function (response) {
                if (response.data) {
                    // 重新加载购物车数据
                    $scope.findCart();
                } else {
                    alert("操作失败！");
                }

            });
        }else {
            alert("购买数量不能小于1")
            // 重新加载购物车数据
            $scope.findCart();
        }
    }

    }
)