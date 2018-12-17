/** 定义控制器层 */
app.controller('userController', function ($scope, baseService) {
    /** 定义用户对象 */
    $scope.user = {};
    /** 定义保存用户方法 */
    $scope.save = function () {
        if ($scope.rePassword != $scope.user.password) {
            alert("密码不一致，请重新输入！");
            return;
        }
        baseService.sendPost("/user/save?smsCode=" + $scope.smsCode, $scope.user).then(function (response) {
            if (response.data) {
                alert("注册成功！")
                //清空数据
                $scope.user = {};
                $scope.rePassword = "";
                $scope.smsCode = ";"
            } else {
                alert("注册失败！")
            }
        });
    };

    /** 定义发送短信验证码方法 */
    $scope.sendCode = function () {
        if ($scope.user.phone && /^1[3|4|5|6|8]\d{9}$/.test) {
            //发送异步请求
            baseService.sendPost("/user/sendCode?phone=" + $scope.user.phone).then(function (response) {
                if(response.data){
                    alert("发送成功！")
                }else {
                    alert("发送失败！")
                }

            });
        } else {
            alert("手机号码无效！！")
        }
    };

})
;