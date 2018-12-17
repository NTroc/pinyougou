/** 定义搜索控制器 */
app.controller("searchController", function ($scope, $sce, $controller, $location, baseService) {

    /**  指定继承baseController*/
    $controller('baseController', {$scope: $scope});

    /** 定义搜索参数对象 */
    $scope.searchParam = {
        keywords: '',
        category: '',
        brand: '',
        price: '',
        spec: {},
        page: 1,
        rows: 10,
        sortField: '',
        sort: ''
    };

    /** 定义搜索方法 */
    $scope.search = function () {
        baseService.sendPost("/Search", $scope.searchParam)
            .then(function (response) {
                /** 获取搜索结果 */
                $scope.resultMap = response.data;

                // 定义关键字中间变量
                $scope.keyword = $scope.searchParam.keywords;

                /** 调用初始化页码方法 */
                initPageNum();
            });
    };

    // 把html格式的字符串转化成html是标签
    $scope.trustHtml = function (html) {
        // html格式的字符串
        return $sce.trustAsHtml(html);
    };

    // 定义封装过滤条件参数方法
    $scope.addSearchItem = function (key, value) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchParam[key] = value;
        } else {
            $scope.searchParam.spec[key] = value;
        }
        //执行搜索
        $scope.search();
    }

    // 定义删除过滤条件的方法
    $scope.removeSearchItem = function (key) {
        if (key == 'category' || key == 'brand' || key == 'price') {
            $scope.searchParam[key] = '';
        } else {
            delete $scope.searchParam.spec[key];
        }
        //执行搜索
        $scope.search();
    }

    /** 定义初始化页码方法 */
    var initPageNum = function () {
        // 定义页码数组
        $scope.pageNums = [];
        // 定义开始页码
        var firstPage = 1;
        // 定义结束页码
        var lastPage = $scope.resultMap.totalPages;

        //定义前面加点
        $scope.firstDot = true;
        //定义后面加点
        $scope.lastDot = true;

        // 判断总页码是不是大于5
        if ($scope.resultMap.totalPages > 5) {// 控制firstPage、lastPage
            // 判断当前页码是否靠首页近些(尽量生成前面的页码)
            if ($scope.searchParam.page < 4) {
                lastPage = 5;
                $scope.firstDot = false;
            }
            // 判断当前页码是否靠尾页近些(尽量生成后面的页码)
            else if ($scope.searchParam.page > $scope.resultMap.totalPages - 3) {
                firstPage = $scope.resultMap.totalPages - 4;
                $scope.lastDot = false;
            }
            else {
                firstPage = $scope.searchParam.page - 2;
                lastPage = $scope.searchParam.page + 2;
            }
        } else {
            $scope.firstDot = false;
            $scope.lastDot = false;
        }
        /** 循环产生页码 */
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageNums.push(i);
        }
    }

    // 根据页码查询方法
    $scope.pageSearch = function (page) {
        // 转化成number类型
        var page = parseInt(page)
        // 判断页码有效性
        // 页码大于等于1 并且 小于等于总页数 并且 不能等于当前页
        if (page >= 1 && page <= $scope.resultMap.totalPages && page != $scope.searchParam.page) {
            // 设置页码参数
            $scope.searchParam.page = page;
            // 执行搜索
            $scope.search();
        }
    }

    /** 定义排序搜索方法 */
    $scope.sortSearch = function (sortField, sort) {
        $scope.searchParam.sortField = sortField;
        $scope.searchParam.sort = sort;
        // 执行搜索
        $scope.search();
    };

    // 获取首页传过来的参数keywords
    $scope.getKeywords = function () {
        // ?keywords=小米
        // location.search
        //$location.search() 获取请求url？后面的参数，是json对象
        $scope.searchParam.keywords = $location.search().keywords;
        // 执行搜索
        $scope.search();
    }
});
