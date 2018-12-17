/** 定义控制器层 */
app.controller('sellerController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});


    /** 查询当前登陆用户信息 */
    $scope.findSellerBySellerId = function () {
        baseService.sendGet("/seller/findSellerBySellerId").then(function (response) {
            $scope.seller = response.data;
        });
    }


    /** 查询条件对象 */
    $scope.searchEntity = {};
    /** 分页查询(查询条件) */
    $scope.search = function (page, rows) {
        baseService.findByPage("/seller/findByPage", page,
            rows, $scope.searchEntity)
            .then(function (response) {
                /** 获取分页查询结果 */
                $scope.dataList = response.data.rows;
                /** 更新分页总记录数 */
                $scope.paginationConf.totalItems = response.data.total;
            });
    };

    /** 添加 */
    $scope.saveOrUpdate = function () {
        baseService.sendPost("/seller/save", $scope.seller)
            .then(function (response) {
                if (response.data) {
                    /** 跳转到商家登录页面 */
                    location.href = "/shoplogin.html"
                } else {
                    alert("操作失败！");
                }
            });
    };

    /** 修改 */
    $scope.update = function () {
        baseService.sendPost("/seller/update", $scope.seller)
            .then(function (response) {
                if (response.data) {
                    alert("修改成功！");
                } else {
                    alert("操作失败！");
                }
            });
    };


    /** 修改密码 */
    $scope.updatePassword = function () {
        if ($scope.newPassword == $scope.rePassword) {
            baseService.sendGet("/seller/updatePassword?oldPassword="+$scope.oldPassWord+"&newPassword=" + $scope.newPassword)
                .then(function (response) {
                    if (response.data){
                        alert("密码修改成功！")
                        location.href="/shoplogin.html"
                    }else {
                        alert("原密码错误！")
                    }
                });
        }else {
            alert("确认密码与新密码需要一致！")
        }
    }

    /** 批量删除 */
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/seller/delete", $scope.ids)
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