/** 定义订单控制器 */
app.controller("orderController", function ($scope, $controller,$interval,baseService) {
    /** 指定继承cartController */
    $controller("cartController", {$scope: $scope});
    /** 根据登录用户获取地址 */
    $scope.findAddressByUser = function () {
        baseService.sendGet("/order/findAddressByUser")
            .then(function (response) {
                $scope.addressList = response.data;

                $scope.address = $scope.addressList[0];
            });
    };

    /** 选择地址 */
    $scope.selectAddress = function (address) {
        $scope.address = address;
    }

    /** 判断是否是当前选中的地址 */
    $scope.isSelectedAddress = function (address) {
        return $scope.address == address;
    }

    /** 添加别名 */
    $scope.getalias = function ($event) {
        var alias = $(event.target).html()
        $scope.entity.alias = alias;
    }

    /** 显示修改 */
    $scope.show = function (address) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = address;
    }

    /** 保存或修改 */
    $scope.saveOrUpdate = function () {
        //alert(JSON.stringify($scope.entity))
        //定义请求URL
        var url = "saveAddress"; // 添加品牌
        if ($scope.entity.id) {
            url = "updateAddress";// 修改品牌
        }
        baseService.sendPost("/order/" + url, $scope.entity)
            .then(function (response) {
                //判断响应数据
                if (response.data) {
                    //重新查询数据
                    $scope.findAddressByUser();
                } else {
                    alert("操作失败")
                }
            });
    }

    /** 删除地址 */
    $scope.delete = function (addressId) {
        baseService.sendGet("/order/deleteAddress?addressId=" + addressId).then(function (response) {
            //判断响应数据
            if (response.data) {
                //重新查询数据
                $scope.findAddressByUser();
            } else {
                alert("操作失败")
            }
        });
    }

    /** 定义order对象封装参数 */
    $scope.order = {paymentType: '1'};
    /** 选择支付方式 */
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    }

    /** 保存订单 */
    $scope.saveOrder = function () {
        // 设置收件人地址
        $scope.order.receiverAreaName = $scope.address.address;
        // 设置收件人手机号码
        $scope.order.receiverMobile = $scope.address.mobile;
        // 设置收件人
        $scope.order.receiver = $scope.address.contact;
        //发送异步请求
        baseService.sendPost("/order/save", $scope.order).then(function (response) {
            if (response.data) {
                // 如果是微信支付，跳转到扫码支付页面
                if ($scope.order.paymentType == 1) {
                    location.href = "/order/pay.html";
                } else {
                    // 如果是货到付款，跳转到成功页面
                    location.href = "/order/paysuccess.html";
                }
            } else {
                alert("订单提交失败！");
            }
        });
    }

    /** 生成微信支付二维码 */
    $scope.genPayCode = function(){
        baseService.sendGet("/order/genPayCode").then(function(response){
            /** 获取金额(转化成元) */
            $scope.money = (response.data.totalFee / 100).toFixed(2);
            /** 获取订单交易号 */
            $scope.outTradeNo= response.data.outTradeNo;
            /** 获取微信支付的URL */
            $scope.codeUrl = response.data.codeUrl;
            /** 生成二维码 */
          /*  var qr = new QRious({
                element : document.getElementById('qrious'),
                size : 250,
                level : 'H',
                value : response.data.codeUrl
            });*/
            document.getElementById("qrious").src="/barcode?url=" + $scope.codeUrl;

            /**
             * 开启定时器
             * 第一个参数：回调函数
             * 第二个参数：间隔的毫秒数3秒
             * 第三个参数：总调用次数 100次(5分钟)
             */
            var timer = $interval(function () {
                // 发送异步请求
                baseService.sendGet("/order/queryPayStatus?outTradeNo="+$scope.outTradeNo)
                    .then(function (response) {
                        // 获取响应数据: {status : 1|2|3} 1:支付成功、2:未支付、3:支付失败
                        if (response.data.status == 1){
                            // 取消定时器
                            $interval.cancel(timer);
                            // 支付成功，跳转到支付成功的页面
                            location.href = "/order/paysuccess.html?money=" + $scope.totalFee;
                        }
                        if (response.data.status == 3){
                            // 取消定时器
                            $interval.cancel(timer);
                            // 支付夫败，跳转到支付失败的页面
                            location.href = "/order/payfail.html";
                        }
                });
            },3000,100)

            // 总调用次数结束后，需要回调函数
            timer.then(function(){
                // 关闭订单
                $scope.codeStr = "二维码已过期，不能支付！";
            });

        });
    };

    /** 获取支付金额 */
    $scope.getMoney = function () {
        return $location.search().money;
    }

});