# 第三版
实现逻辑：使用spring-security的权限注解进行权限鉴定，不再使用接口url配置。
好处：更加灵活、安全，前端用户不能通过接口就能知道属于什么权限的接口

## 配置解析
* prePostEnabled = true 的作用的是启用Spring Security的@PreAuthorize 以及@PostAuthorize 注解。
* securedEnabled = true 的作用是启用Spring Security的@Secured 注解。
* jsr250Enabled = true 的作用是启用@RoleAllowed 注解

## JSR-250注解
遵守了JSR-250的标准注解 主要注解
* @DenyAll --拒绝
* @RolesAllowed --允许指定的角色进行访问.
* @PermitAll --通过

## securedEnabled注解
主要注解
* @Secured

>1. @Secured注解规定了访问访方法的角色列表，在列表中最少指定一种角色 <br />
>2. @Secured在方法上指定安全性，要求 角色/权限等 只有对应 角色/权限 的用户才可以调用这些方法。 如果有人试图调用一个方法，但是不拥有所需的 角色/权限，那会将会拒绝访问将引发异常。
>3. @Secured不支持Spring EL表达式, 即ROLE_user和user是两种不同的角色

## @prePostEnabled注解

这个开启后支持Spring EL表达式 算是蛮厉害的。如果没有访问方法的权限，会抛出AccessDeniedException。

主要注解
* @PreAuthorize --适合进入方法之前验证授权
* @PostAuthorize --检查授权方法之后才被执行并且可以影响执行方法的返回值
* @PostFilter --在方法执行之后执行，而且这里可以调用方法的返回值，然后对返回值进行过滤或处理或修改并返回
* @PreFilter --在方法执行之前执行，而且这里可以调用方法的参数，然后对参数值进行过滤或处理或修改

## @PreAuthorize注解使用
@PreAuthorize("hasRole('ROLE_VIEWER')") 相当于 @RolesAllowed(“ROLE_VIEWER”)。

@PreAuthorize还可接收EL表达式或者函数，使用EL表达式判定方式如下：
```@PreAuthorize("#sysUser.username == 'admin' ")```

常见内置表达式:

| 表达式                                                                   | 描述                                                                                                                                         |
|-----------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| hasRole([role])                                                       | 如果当前主体具有指定角色，则返回true。默认情况下，如果提供的角色不以“ROLE_”开头，则会添加该角色。这可以通过修改DefaultWebSecurityExpressionHandler上的defaultRolePrefix来自定义。                   |
| hasAnyRole([role1,role2])                                             | 如果当前主体具有任何提供的角色（以逗号分隔的字符串列表给出），则返回true。默认情况下，如果提供的角色不以“ROLE_”开头，则会添加该角色。这可以通过修改DefaultWebSecurityExpressionHandler上的defaultRolePrefix来自定义。 |
| hasAuthority([authority])                                             | 如果当前主体具有指定的权限，则返回true。                                                                                                                     |
| hasAnyAuthority([authority1,authority2])                              | 如果当前主体具有任何提供的权限（以逗号分隔的字符串列表给出），则返回true                                                                                                     |
| principal                                                             | 允许直接访问代表当前用户的主体对象                                                                                                                          |
| authentication                                                        | 允许直接访问从SecurityContext获取的当前Authentication对象                                                                                                |
| permitAll                                                             | 始终评估为true                                                                                                                                  |
| denyAll                                                               | 始终评估为false                                                                                                                                 |
| isAnonymous()                                                         | 如果当前主体是匿名用户，则返回true                                                                                                                        |
| isRememberMe()                                                        | 如果当前主体是remember-me用户，则返回true                                                                                                               |
| isAuthenticated()                                                     | 如果用户不是匿名用户，则返回true                                                                                                                         |
| isFullyAuthenticated()                                                | 如果用户不是匿名用户或记住我用户，则返回true                                                                                                                   |
| hasPermission(Object target, Object permission)                       | 如果用户有权访问给定权限的提供目标，则返回true。例如，hasPermission(domainObject, 'read')                                                                           |
| hasPermission(Object targetId, String targetType, Object permission)  | 如果用户有权访问给定权限的提供目标，则返回true。例如，hasPermission(1, 'com.example.domain.Message', 'read')                                                        |


## @PreFilter以及@PostFilter注解使用
Spring Security提供了一个@PreFilter 注解来对传入的参数进行过滤：
```
@PreFilter("filterObject != authentication.principal.username")
public String joinUsernames(List<String> usernames) {
    return usernames.stream().collect(Collectors.joining(";"));
}
```
当usernames中的子项与当前登录用户的用户名不同时，则保留；当usernames中的子项与当前登录用户的用户名相同时，则移除。比如当前使用用户的用户名为zhangsan，此时usernames的值为{"zhangsan", "lisi", "wangwu"}，则经@PreFilter过滤后，实际传入的usernames的值为{"lisi", "wangwu"}

如果执行方法中包含有多个类型为Collection的参数，filterObject 就不太清楚是对哪个Collection参数进行过滤了。此时，便需要加入 filterTarget 属性来指定具体的参数名称：
```
@PreFilter(value = "filterObject != authentication.principal.username", filterTarget = "usernames")
public String joinUsernamesAndRoles(List<String> usernames, List<String> roles) {
  
    return usernames.stream().collect(Collectors.joining(";")) 
      + ":" + roles.stream().collect(Collectors.joining(";"));
}
```
同样的我们还可以使用@PostFilter 注解来过返回的Collection进行过滤：
```
@PostFilter("filterObject != authentication.principal.username")
public List<String> getAllUsernamesExceptCurrent() {
    return userRoleRepository.getAllUsernames();
}
```
此时 filterObject 代表返回值。如果按照上述代码则实现了：移除掉返回值中与当前登录用户的用户名相同的子项。

## 自定义元注解
如果我们需要在多个方法中使用相同的安全注解，则可以通过创建元注解的方式来提升项目的可维护性。

比如创建以下元注解：
```
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_VIEWER')")
public @interface IsViewer {
}
```
然后可以直接将该注解添加到对应的方法上：
```
@IsViewer
public String getUsername4() {
    //...
}
```
在生产项目中，由于元注解分离了业务逻辑与安全框架，所以使用元注解是一个非常不错的选择。

## 类上使用安全注解
如果一个类中的所有的方法我们全部都是应用的同一个安全注解，那么此时则应该把安全注解提升到类的级别上：
```
@Service
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SystemService {
  
    public String getSystemYear(){
        //...
    }
  
    public String getSystemDate(){
        //...
    }
}
```
上述代码实现了：访问getSystemYear 以及getSystemDate 方法均需要ROLE_ADMIN权限。

## 方法上应用多个安全注解
在一个安全注解无法满足我们的需求时，还可以应用多个安全注解:
```
@PreAuthorize("#username == authentication.principal.username")
@PostAuthorize("returnObject.username == authentication.principal.nickName")
public CustomUser securedLoadUserDetail(String username) {
    return userRoleRepository.loadUserByUserName(username);
}
```
此时Spring Security将在执行方法前执行@PreAuthorize的安全策略，在执行方法后执行@PostAuthorize的安全策略。

## 总结
在此结合我们的使用经验，给出以下两点提示：

1. 默认情况下，在方法中使用安全注解是由Spring AOP代理实现的，这意味着：如果我们在方法1中去调用同类中的使用安全注解的方法2，则方法2上的安全注解将失效。

2. Spring Security上下文是线程绑定的，这意味着：安全上下文将不会传递给子线程。

```
public boolean isValidUsername4(String username) {
    // 以下的方法将会跳过安全认证
    this.getUsername();
    return true;
}
```
