/** 定义首页控制器层 */
app.controller("indexController", function ($scope, $controller, baseService) {
    /**  指定继承baseController*/
    $controller('baseController', {$scope: $scope});

    /** 根据分类id查询广告 */
    $scope.findContentByCategoryId = function (categoryId) {
        baseService.sendGet("/content/findContentByCategoryId?categoryId=" + categoryId)
            .then(function (response) {
                $scope.contentList = response.data;
            });
    }

    // 为搜索按钮绑定点击事件
    $scope.search = function () {
        // 判断搜索的关键字
        var keywords = $scope.keywords ? $scope.keywords : "";
        // 跳转到搜索系统
        location.href = "http://search.pinyougou.com?keywords=" + keywords;
    };

});