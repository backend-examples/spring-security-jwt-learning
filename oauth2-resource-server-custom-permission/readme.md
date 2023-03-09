## @ControllerAdvice

### 优缺点
* 优点：将 Controller 层的异常和数据校验的异常进行统一处理，减少模板代码，减少编码量，提升扩展性和可维护性。
* 缺点：只能处理 Controller 层未捕获（往外抛）的异常，对于 Interceptor（拦截器）层的异常，Spring 框架层的异常，就无能为力了。同时，调用服务层异常必须手动抛出，controller层不捕获，则会自动被ControllerAdvice捕获处理。