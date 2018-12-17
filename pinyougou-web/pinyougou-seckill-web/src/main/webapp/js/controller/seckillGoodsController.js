/** 定义秒杀商品控制器 */
app.controller("seckillGoodsController", function ($scope, $controller, $location, $timeout, baseService) {

    /** 指定继承cartController */
    $controller("baseController", {$scope: $scope});

    /** 查询秒杀的商品集合 */
    $scope.findSeckillGoods = function () {
        baseService.sendGet("/seckill/findSeckillGoods").then(function (response) {
            $scope.seckillGoodsList = response.data;
        });
    }

    /** 根据秒杀商品i的查询该商品 */
    $scope.findOne = function () {
        // 获取请求URL后面的参数
        var id = $location.search().id;
        baseService.sendGet("/seckill/findOne?id=" + id).then(function (response) {
            $scope.entity = response.data;
            /** 调用倒计时方法 */
            $scope.downcount($scope.entity.endTime);

        });
    }

    /** 倒计时的方法 */
    $scope.downcount = function (endTime) {
        /**  计算出相差的毫秒数 */
            // endTime: 毫秒数
            // 计算出结束时间与当前时间相差的毫秒数
        var millsSeconds = endTime - new Date().getTime();
        // 计算出相差的秒数
        var seconds = Math.floor(millsSeconds / 1000);
        if (seconds > 0) {
            // 通过秒数，产生  小时：分钟：秒
            // 计算出相差的分钟
            var minutes = Math.floor(seconds / 60);
            // 计算出相差的小时
            var hours = Math.floor(minutes / 60);
            // 计算出相差的天数
            var days = Math.floor(hours / 24);

            // 定义数组拼接时间显示的字符串
            var resArr = [];
            if (days > 0) {
                resArr.push(days + "天 ")
            }
            if (hours > 0) {
                resArr.push(calc(hours - days * 24) + ":");
            }
            if (minutes > 0) {
                resArr.push(calc(minutes - hours * 60) + ":");
            }
            resArr.push(calc(seconds - minutes * 60) + ":");

            // 将数组中的元素转化成字符串
            $scope.timeStr = resArr.join("");

            // 开启定时器
            $timeout(function () {
                $scope.downcount(endTime);
            }, 1000);
        } else {
            $scope.timeStr = "秒杀商品已结束！";
        }
    };

    // 计算的函数
    var calc = function (num) {
        return num > 9 ? num : "0" + num;
    };

    /** 提交订单 */
    $scope.submitOrder = function () {
        // 判断用户是否登录
        if ($scope.loginName) { // 已登录
            baseService.sendGet("/order/submitOrder?id="+$scope.entity.id).then(function (response) {
                if (response.data){
                    // 下单成功，跳转到支付页面
                    location.href = "/order/pay.html";
                }else {
                    alert("下单失败！");
                }
            });
        } else { // 未登录
            // 跳转到单点登录系统
            location.href = "http://sso.pinyougou.com?service=" + $scope.redirectUrl;
        }
    };


});