/** 定义控制器层 */
app.controller('goodsController', function ($scope, $controller, baseService) {

    /** 指定继承baseController */
    $controller('baseController', {$scope: $scope});


    /** 添加或修改 */
    $scope.saveOrUpdate = function () {
        /*获取富文本编辑器内容*/
        $scope.goods.goodsDesc.introduction = editor.html();

        /** 发送post请求 */
        baseService.sendPost("/goods/save", $scope.goods)
            .then(function (response) {
                if (response.data) {
                    alert("保存成功")
                    //清空表单
                    $scope.goods = {}
                    //清空富文本编辑器
                    editor.html("");
                } else {
                    alert("操作失败！");
                }
            });
    };

    //定义商品数据存储格式
    //$scope.goods.goodsDesc.itemImages=[];  错误的初始化
    $scope.goods = {goodsDesc: {itemImages: [], specificationItems: []}};


    //图片异步上传
    $scope.uploadFile = function () {
        //调用服务层方法
        baseService.uploadFile().then(function (response) {
            //获取响应数据
            //显示要上传的图片{url:'',status:''}
            if (response.data.status == 200) {
                //获得图片url
                $scope.picEntity.url = response.data.url;
            } else {
                alert("上传图片失败")
            }
        })
    }

    //添加图片到数组
    $scope.addImage = function () {
        $scope.goods.goodsDesc.itemImages.push($scope.picEntity);
    }

    //删除图片到数组
    $scope.deleteImage = function (idx) {
        $scope.goods.goodsDesc.itemImages.splice(idx, 1);
    }

    //给了据父级id查询商品分类
    $scope.findItemCatByParentId = function (parentId, name) {
        baseService.sendGet("/itemCat/findItemCatByParentId?parentId=" + parentId).then(function (response) {
            //获取相应数据List<ItemCat>
            $scope[name] = response.data;
        })
    }

    /** 监控 goods.category1Id 变量，查询二级分类 */
    $scope.$watch("goods.category1Id", function (newVel, oldVel) {
        // 判断newVal不是undefined
        if (newVel) {
            $scope.findItemCatByParentId(newVel, 'itemCatList2');
        } else {
            $scope.itemCatList2 = [];
        }
    })

    /** 监控 goods.category2Id 变量，查询三级分类 */
    $scope.$watch("goods.category2Id", function (newVel, oldVel) {
        if (newVel) {
            $scope.findItemCatByParentId(newVel, 'itemCatList3');
        } else {
            $scope.itemCatList3 = [];
        }
    })

    /** 监控 goods.category3Id 变量，查询模板类型 */
    $scope.$watch("goods.category3Id", function (newVel, oldVel) {
        if (newVel) {
            //循环三级分类数组 List<ItemCat> : [{},{}]
            for (var i = 0; i < $scope.itemCatList3.length; i++) {
                //取一个元素
                var itemCat = $scope.itemCatList3[i];
                if (itemCat.id == newVel) {
                    $scope.goods.typeTemplateId = itemCat.typeId;
                    break;
                }
            }
        } else {
            $scope.goods.typeTemplateId = null;
        }
    })

    /** 监控 goods.typeTemplateId 模板ID，查询该模版对应的品牌 */
    $scope.$watch("goods.typeTemplateId", function (newVel, oldVel) {
        if (newVel) {
            baseService.findOne("/typeTemplate/findOne", newVel).then(function (response) {
                // 获取响应数据 TypeTemplate {}
                // 获取品牌数据 [{},{}]
                $scope.brandIds = JSON.parse(response.data.brandIds);

                // 获取响应数据 TypeTemplate {}
                // 获取拓展属性数据 [{"text":"分辨率"},{"text":"摄像头"}]
                //需要得到数据 goods.goodsDesc.customAttributeItems [{"text":"观看距离","value":"3-3.5米"},{}]
                $scope.goods.goodsDesc.customAttributeItems = JSON.parse(response.data.customAttributeItems);

            })

            /** 查询该模版对应的规格与规格选项 */
            baseService.findOne("/typeTemplate/findSpecByTemplateId", newVel).then(function (response) {
                /**
                 * [{"id":27,"text":"网络", "options" : [{},{}]},
                 {"id":32,"text":"机身内存", "options" : [{},{}]}]
                 */
                $scope.specList = response.data;
            });
        } else {
            $scope.brandIds = [];
        }
    })


    /** 给复选框绑定点击事件 */
    $scope.updateSpecAttr = function ($event, specName, optionName) {
        //[{"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"},
        // {"attributeValue":["64G","128G"],"attributeName":"机身内存"}]

        /** 根据json对象的key到json数组中搜索该key值对应的对象 */
        var obj = $scope.searchJsonByKey($scope.goods.goodsDesc.specificationItems, 'attributeName', specName);
        if (obj) {
            /** 判断checkbox是否选中 */
            if ($event.target.checked) {
                /** 添加该规格选项到数组中 */
                obj.attributeValue.push(optionName);
            } else {
                /** 取消勾选，从数组中删除该规格选项 */
                obj.attributeValue.splice(obj.attributeValue.indexOf(optionName), 1)
                if (obj.attributeValue.length == 0) {
                    //获取json对象中数组的索引号
                    var idx = $scope.goods.goodsDesc.specificationItems.indexOf(obj)
                    /** 全部取消勾选，删除数组 */
                    $scope.goods.goodsDesc.specificationItems.splice(idx, 1);
                }
            }
        } else {
            /** 如果为空，则新增数组元素 */
            $scope.goods.goodsDesc.specificationItems.push({"attributeValue": [optionName], "attributeName": specName})
        }
    }

    /** 根据json对象的key到json数组中搜索该key值对应的对象 */
    $scope.searchJsonByKey = function (jsonArr, key, value) {
        for (i = 0; i < jsonArr.length; i++) {
            if (jsonArr[i][key] == value) {
                return jsonArr[i];
            }
        }
        return null;
    }

    $scope.createItems = function () {
        /** 定义SKU数组变量，并初始化 */
        $scope.goods.items = [{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'}];
        // 获取用户选中的规格选项
        var specItems = $scope.goods.goodsDesc.specificationItems
        /**
         * [{"attributeValue":["联通4G","移动4G","电信4G"],"attributeName":"网络"}]
         */
        for (var i = 0; i < specItems.length; i++) {
            //获取一个数组元素
            var obj = specItems[i];
            //调用扩充原SKU数组的方法
            $scope.goods.items = $scope.swapItems($scope.goods.items, obj.attributeValue, obj.attributeName);
        }
    }

    /*扩充原SKU数组的方法*/
    $scope.swapItems = function (itemsArr, attributeValueArr, attributeName) {
        //attributeValueArr:["联通4G","移动4G","电信4G"]
        //attributeName:"网络"
        //[{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'},{},{}]
        //定义新的SKU数组
        var newItems = [];
        for (var i = 0; i < itemsArr.length; i++) {
            //取一个元素
            //{spec: {}, price: 0, num: 9999, status: '0', isDefault: '0'},{},{}
            var item = itemsArr[i];
            //遍历 "attributeValue":["联通4G","移动4G","电信4G"]
            for (var j = 0; j < attributeValueArr.length; j++) {
                //产生新的SKU对象 //spec:{"网络":"联通4G","机身内存":"64G"}
                var newItem = JSON.parse(JSON.stringify(item));
                newItem.spec[attributeName] = attributeValueArr[j];//取一个元素"联通4G"
                //添加到新的SKU数组
                newItems.push(newItem)
            }
        }
        return newItems;

    }


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


    /** 显示修改 */
    $scope.show = function (entity) {
        /** 把json对象转化成一个新的json对象 */
        $scope.entity = JSON.parse(JSON.stringify(entity));
    };


    /** 批量删除 */
    $scope.delete = function () {
        if ($scope.ids.length > 0) {
            baseService.deleteById("/goods/delete", $scope.ids)
                .then(function (response) {
                    if (response.data) {
                        /** 清空ids数组 */
                        $scope.ids = [];
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