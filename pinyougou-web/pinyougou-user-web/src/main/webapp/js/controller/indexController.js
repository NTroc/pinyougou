/** 首页控制器 */
app.controller("indexController", function ($scope, baseService) {
    /** 获取登录用户名 */
    $scope.showName = function () {
        baseService.sendGet("/user/showName").then(function (response) {
            $scope.loginName = response.data.loginName
        });
    }
})