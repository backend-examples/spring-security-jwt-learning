package com.security.redis;

/**
 * 验证码资源处理
 *
 * @author echo
 * @date 2019/7/28 下午10:44
 */
public interface ValidateCodeRepository {

    /**
     * 保存
     *
     * @param request 请求
     * @param code    验证码
     * @param type    类型
     */
    void save(String phone, String code, String type);

    /**
     * 获取
     *
     * @param request 请求
     * @param type    类型
     * @return 验证码
     */
    String get(String phone, String type);

    /**
     * 移除
     *
     * @param request 请求
     * @param type    类型
     */
    void remove(String phone, String type);


}
