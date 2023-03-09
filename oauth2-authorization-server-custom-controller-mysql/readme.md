## 异常注意点

### 1. 自定义controller的异常处理

自定义controller的异常不会被HandlerInterceptor中的afterCompletion捕获，也就是不会被oauth2的异常翻译器捕获，调试发现afterCompletion的异常为null。
此时异常会被@ControllerAdvice中的全局异常捕获。还有，需要通过response.setStatus设置对应的响应状态码，否则会导致即使返回了错误数据，
前端请求接口依然得到的是200。

### 2. 授权码模式验证

#### a. 获取授权码
请求方法类型：GET
```http request
http://localhost:8001/author/oauth/authorize?client_id=client3&redirect_uri=http://localhost:8080&response_type=code&scope=custom
```

请求参数：
> * client_id: 客户端id
> * redirect_uri：重定向uri
> * response_type：响应类型，此处固定为code
> * scope: 权限范围

#### b. 获取token
请求方法类型：POST
```http request
http://localhost:8080/author/auth/token
```

请求参数：
> * grant_type: authorization_code 授权码类型
> * redirect_uri: 上一步的redirect_uri
> * scope：上一步的scope 可选，默认为空，可访问所有
> * client_id： 上一步的client_id
> * client_secret：客户端密钥
> * code: 上一步获取的code

### 3. 密码模式
#### 获取token
请求类型：POST
```http request
http://localhost:8080/author/auth/token
```

请求参数：
> * grant_type: password 密码类型
> * client_id: 客户端id
> * client_secret：客户端密钥
> * username: 登录用户名
> * password: 登录密码

## 自定义controller

认证服务器和资源服务器分离的情况下，自定义controller只有被security放行才能被访问。要想开发需要登录认证后才能访问的接口，只能搭建对应的资源服务器。