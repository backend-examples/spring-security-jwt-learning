package com.security.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.util.TypeUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.Charset;

/**
 * 自定义redis序列化方式
 *
 * @author qinxuewu
 * @version 1.00
 * @time 8/8/2018下午 12:39
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private Class<T> clazz;

    static {
        // 全局开启AutoType, autoType类型默认时被fastjson禁用掉了的，这里进行开启
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        //如果遇到反序列化autoType is not support错误，请添加并修改一下包名到bean文件路径
        // ParserConfig.getGlobalInstance().addAccept("com.xxxxx.xxx");
        // 建议使用这种方式，小范围指定白名单
//         ParserConfig.getGlobalInstance().addAccept("me.zhengjie.domain");
        ParserConfig.getGlobalInstance().addAccept("org.springframework.security.core.authority.");
        TypeUtils.addMapping("org.springframework.security.core.authority.SimpleGrantedAuthority",
                SimpleGrantedAuthority.class);

    }

    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
//        SerializerFeature.WriteClassName
        return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        return JSON.parseObject(str, clazz);
    }

}