## 授权登录注意点

1. oauth2授权登录必须要传递`clientId`和`clientSecret`,可以直接参数传递。也可以使用`Authorization`请求头传递，格式为`Basic clientId:clientSecret(Base64编码)`，所以非第三方登录，前端需要提前拿到当前网站的clientId和clientSecret进行Base64编码，作为请求传递。一个网站的clientId和clientId是固定的
2. 自定义授权模式必须自定义实现`AbstractTokenGranter`，可选实现`AbstractAuthenticationToken`和`AuthenticationProvider`。校验流程是`AbstractTokenGranter`(内部生成`AbstractAuthenticationToken`对象)->`AuthenticationProvider`
3. 所有授权模式都必须使用`UserDetailsService`的`loadUserByUsername`去获取用户信息。要想使用不同的keyword获取数据库中的信息，在`loadUserByUsername`使用条件区分。