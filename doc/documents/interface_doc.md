短讯通客户端接口
----------

**说明:**
```
${domain}=http://android.oilchem.net;应用根路径
${accessToken}=accessToken;令牌
stat：结果状态码,1表示成功，0表示失败
error：stat为0时，提供错误信息
data：数据内容
```

### 用户登录###

**url:** `${domain}/user/userLogin.do`
**请求参数：**
```
username：用户名
password：md5后的密码
imei：设备唯一标识码
applePush:ios设备必选参数,1表ios设备;留空或不传表示其他设备
```

**结果数据:**
```
{"stat": 1, "error": "", "data": {
    "login": "1",//0: 登录失败  1: 登录成功
    "username": "张三", //用户实名
    "message": "登录成功", //如果登录失败则返回失败原因
    "accessToken": "1b4df9c2a9b14e7e92438aa56f7c43ed" //返回的令牌
} }
```


----------

### iOS更新deviceToken###

**url:** `${domain}/user/updateDeviceToken.do`
**请求参数：**
```
username：用户名
deviceToken：deviceToken设备令牌串
accessToken：用户验证令牌
```

**结果数据:**
```
{"stat": 1, "error": "", "data": {
    "login": "1",
    "message": "更新deviceToken成功",
    "accessToken": "04c0a30aee7841349363e3b36e62360d"
} }
```

----------


### 用户注册###

*注册，请求验证码只支持 **Post** 方式; 在本Request之前，需要通过手机获取一个验证码*
####请求手机验证码####
**请求地址:** 
```
http://www.oilchem.net/reg/getcode/?clientid=UserName&UserName=${用户手机号码}
```
**结果数据:**
```
{"stat": "1","error": "","data": {"getcode": "0","message": "请输入正确的手机号"}} 
{"stat": "1","error": "","data": {"getcode": "0","message": "手机号码已经被其他人注册！"}} 
{"stat": "1","error": "","data": {"getcode": "1","message": "确认码已经发送到13000000012，请稍候查收。"}}
```
**示例如图：**
![demo](http://android.oilchem.net/api/aa.png)


####注册####
**url:** `http://www.oilchem.net/reg/?action=android`
**请求参数：**
```
UserName: 用户手机号码
vkey: 验证码，用于防止恶意注册
PassWord: 密码
```

**结果数据:**
```
{"stat": "1","error": "","data": {"register": "0","message": "请输入正确的手机号"}} 
{"stat": "1","error": "","data": {"register": "0","message": "注册确认码输入错误！"}} 
{"stat": "1","error": "","data": {"register": "0","message": "手机已经注册短讯用户！"}} 
{"stat": "1","error": "","data": {"register": "1","message": "注册成功。请等待客服联系，为您开通功能。"}}
```


----------


### 检查更新###

**url:** `${domain}/user/updateApp.do`
**请求参数：**
```
version: 当前应用版本号(如：1.0)
```

**结果数据:**
```
{
    "stat": "1",
    "error": "",
    "data": {
        "update": "1", //0: 不需要更新  1: 需要更新
        "downloadUrl": "http://www.oilchem.net/download/android/sms.apk" //如果update为0,则不需要更新,这里返回空即可.
    }
}
```

----------


### 注销用户登录###

**url:** `${domain}/user/userLogout.do`
**请求参数：**
```
username: 用户名
accessToken: 用户登录后所有请求均验证此token, 确保该用户身份
```

**结果数据:**
```
{
    "stat": "1",
    "error": "",
    "data": {
        "logout": "1" //0: 注销失败  1: 注销成功(前端移除用户的accessToken)
    }
}
```


----------


### 获得配置信息###
*用户每次打开应用均需要同步服务器的config状态*

**url:** `${domain}/sms/getConfig.do`
**请求参数：**
```
accessToken: 用户登录后所有请求均验证此token, 确保该用户身份
```

**结果数据:**
```
{
    "stat": "1",
    "error": "",
    "data": {
        "accessToken":"a3ab8d5f2d2041709954490d323c044a",//当前最新的令牌
        "config": {
            "categories": [ //用户订阅的分类以及是否接收推送信息
                {
                    "groupId": "123",
                    "name": "成品油东营利津", //分类名称
                    "allowPush": "1" //0:不接收push, 1:接收push
                },
                {"groupId": "123", "name": "成品油东营利津","allowPush": "1"},
                {"groupId": "123", "name": "成品油东营利津","allowPush": "1"},
                {"groupId": "123", "name": "成品油东营利津","allowPush": "1"},
                {"groupId": "123", "name": "成品油东营利津","allowPush": "1"}
            ],
            "customerServicename": "隆众客服",
            "customerServiceNumber": "0533-2591688",
            "pageSizeWhileSearchingLocalSMS": "10",
            "latestAppVersion": "1.0", //如果此版本大于本地版本，则跳出提示框来通过appDownload地址升级
            "appDownload": "http://www.oilchem.net/download/android/sms.apk",
            "getPushTimeInterval": "30" //获取push内容的轮询请求时间间隔 ,30指的是30秒
        }
    }
}
```

----------


### 获取短讯组###

**url:** `${domain}/sms/getCategories.do`
**请求参数：**
```
accessToken: 用户登录后所有请求均验证此token, 确保该用户身份
```

**结果数据:**
```
{
    "stat": "1",
    "error": "",
    "data": {
        "accessToken":"a3ab8d5f2d2041709954490d323c044a",//当前最新的令牌
        "categories": [ //用户订阅的分类以及是否接收推送信息
            {
                "groupId": "123",
                "name": "成品油东营利津", //分类名称
                "allowPush": "1" //0:不接收push, 1:接收push
            },
            {"groupId": "123", "name": "成品油东营利津","allowPush": "1"},
            {"groupId": "123", "name": "成品油东营利津","allowPush": "1"},
            {"groupId": "123", "name": "成品油东营利津","allowPush": "1"},
            {"groupId": "123", "name": "成品油东营利津","allowPush": "1"}
        ]
    }
}
```

----------

### 获取最新信息###

**url:** `${domain}/sms/getMessages.do`
**请求参数：**
```
accessToken: 用户登录后所有请求均验证此token, 确保该用户身份
ts: 记录最后一次获取信息的时间戳
      1 为空时, 说明是第一次取信息, 则返回最进7天的所有短讯
      2 为1323308943时, 返回大于此时间戳的所有短讯
      3 为1323308943时, 当前时间和此ts的差大于7天, 则认为ts为空
```

**结果数据:**
```
{"stat": "1", "error": "", "data": {
    "accessToken": "94e8abedd5874263a89e30f2ade59c4b",//当前最新的令牌
    "ts": "1406096718367",//本次请求的服务器时间戳
    "messages": [
        {
            "msgId": "1064816",//信息Id，客户回复信息时要传给服务器
            "groupId": "5659",//信息组Id
            "ts": "1399132800000",//信息时间
            "title": "dsafasdaa",//信息标题
            "content": "dsafasdaaabc的风格惺惺惜adfad惺惺笑嘻嘻",//信息内容
            "replies": "",//回复内容字段,客户端需本地保存,服务端默认返回空内容
            "groupName": "正式"//信息组名
        },
        {
            "msgId": "1064815",
            "groupId": "5659",
            "ts": "1399132800000",
            "title": "拉萨市夺aaaaaa的风",
            "content": "拉萨市夺abc的风格aaaaaaaaaaaa",
            "replies": "",
            "groupName": "正式"
        }
    ]
}}
```

----------

### 获取推送信息###

**url:** `${domain}/sms/getPushSMS.do`
**请求参数：**
```
accessToken: 用户登录后所有请求均验证此token, 确保该用户身份
groupIds: 用户接收推送的短讯类别ID号,多个以","分隔,如groupIds=123,234,45,65,75,7
```

**结果数据:**
```
{"stat": "1", "error": "", "data": {
    "accessToken": "94e8abedd5874263a89e30f2ade59c4b",//当前最新的令牌
    "ts": "1406097990518",//本次请求的服务器时间戳
    "messages": [
        {
            "msgId": "1064921",//信息Id，客户回复信息时要传给服务器
            "groupId": "3730",//信息组Id
            "ts": "1406097957797",//信息时间
            "title": "新消息......",//信息标题
            "content": "新消息........",//信息内容
            "replies": "",//回复内容字段,客户端需本地保存,服务端默认返回空内容
            "groupName": "异戊二烯"//信息组名
        },
        {
            "msgId": "1064920",
            "groupId": "3730",
            "ts": "1406097919563",
            "title": "",
            "content": "新消息。。。。",
            "replies": "",
            "groupName": "异戊二烯"
        }
    ]
}}
```

----------

### 获取IOS推送信息###

**url:** `${domain}/sms/getIOSPushSMS.do`
**请求参数：**
```
accessToken: 用户登录后所有请求均验证此token, 确保该用户身份
startTime: 起点时间戳,长整型
endTime：终点时间戳,长整型
allFlag：取所有数据标志.当为1时,取所有组,忽略只取推送组打开了的组;不传或其他值时取用户打开的组的数据
```

**结果数据:**
```
{"stat": "1", "error": "", "data": {
    "accessToken": "94e8abedd5874263a89e30f2ade59c4b",//当前最新的令牌
    "ts": "1406097990518",//本次请求的服务器时间戳
    "messages": [
        {
            "msgId": "1064921",//信息Id，客户回复信息时要传给服务器
            "groupId": "3730",//信息组Id
            "ts": "1406097957797",//信息时间
            "title": "新消息......",//信息标题
            "content": "新消息........",//信息内容
            "replies": "",//回复内容字段,客户端需本地保存,服务端默认返回空内容
            "groupName": "异戊二烯"//信息组名
        },
        {
            "msgId": "1064920",
            "groupId": "3730",
            "ts": "1406097919563",
            "title": "",
            "content": "新消息。。。。",
            "replies": "",
            "groupName": "异戊二烯"
        }
    ]
}}
```

----------

### 更改信息组推送状态###

**url:** `${domain}/sms/changePushStat.do`
**请求参数：**
```
categoryName: 类别名称
allowPush: 本类别是否接收推送信息,0: 不接收 1: 接收
accessToken: 用户登录后所有请求均验证此token, 确保该用户身份
```

**结果数据:**
```
{
    "stat": "1",
    "error": "",
    "data": {
        "accessToken":"a3ab8d5f2d2041709954490d323c044a",
        "categories": [
            {
                "groupId": "123",
                "name": "成品油东营利津", //分类名称
                "allowPush": "1" //0:不接收push, 1:接收push
            }
        ]
    }
}
```

----------

### 搜索信息###

**url:** `${domain}/sms/getMessageTrial.do`
**请求参数：**
```
key: 用户搜索时输入的关键字
ts: 请求timestamp小于ts的条目，如果ts为空则取最新的x条数据，x由getConfig当中的[pageSizeWhileSearchingLocalSMS],决定默认10条
```

**结果数据:**
```
{"stat": "1", "error": "", "data": {
    "ts": "1406098260937",
    "messages": [
        {
            "msgId": "2082039",
            "groupId": "",
            "ts": "1405729431870",
            "title": "东明佳润化工1＃混",
            "content": "东明佳润化工1＃混合二甲苯6650元,2＃混合....",
            "replies": "",
            "groupName": ""
        },
        {
            "msgId": "2081937",
            "groupId": "",
            "ts": "1405666443783",
            "title": "今日华北地区成品油",
            "content": "今日华北地区成品油价格维持稳定...",
            "replies": "",
            "groupName": ""
        },
        ......
    ]
}}
```

----------

### 获取信息的回复###

**url:** `${domain}/sms/getReplies.do`
**请求参数：**
```
accessToken: 用户登录后所有请求均验证此token, 确保该用户身份
id: 信息的id(即msgId)
```

**结果数据:**
```
{"stat": "1", "error": "", "data": {
    "accessToken": "94e8abedd5874263a89e30f2ade59c4b",//当前最新的令牌
    "replies": [
        {
            "username": "15821422479",  //用户名,非用户实名
            "msgId": "1064825", //信息Id
            "reply": "abc你好啊！",//回复内容
            "replyTime": "1399439268560" //回复时间
        },
        {
            "username": "15821422479",
            "msgId": "1064825",
            "reply": "测试回复信息",
            "replyTime": "1399439696220"
        }
    ]
}}
```

----------

### 回复信息###

**url:** `${domain}/sms/pushReply.do`
**请求参数：**
```
accessToken: 用户登录后所有请求均验证此token, 确保该用户身份
msgId: 信息的id(即msgId)
username: 用户名
reply: 回复内容
```

**结果数据:**
```
{"stat": "1", "error": "", "data": {
    "accessToken": "94e8abedd5874263a89e30f2ade59c4b", //当前最新的令牌
    "reply": {
        "username": "15821422479",  //用户名,非用户实名
        "msgId": "1064825",     //信息id
        "reply": "这是一条回复消息",    //回复内容
        "replyTime": "1406099359134"    //回复时间
    }
}}
```