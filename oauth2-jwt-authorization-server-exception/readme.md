## 认证服务器的异常处理

认证服务器的异常处理，最先拦截异常的是翻译器，然后会进入拦截器。
目前方案是翻译器不处理异常，直接抛出，由拦截器处理。

## 项目异常处理机制

security集成oauth2，需要分情况处理异常：
1. 使用security的官方接口，则需要在`HttpSecurity`中配置对应的异常处理。比如登录认证处理器(`authenticationEntryPoint`),权限不足处理器(`accessDeniedHandler`)等。
2. 使用oauth2的官方接口，则需要定义`WebResponseExceptionTranslator`(异常翻译器)的实现类。
3. 重写security、oauth2的官方接口、自定义的其他接口，则需要开发自己捕获对应异常，或者自定义全局异常捕获。