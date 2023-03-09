package com.security.service.impl;

import com.security.service.SessionInformationExpiredStrategyService;
import com.security.utils.ResponseResult;
import com.security.utils.ResponseUtils;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Session过期处理策略
 */
@Service
public class SessionInformationExpiredStrategyServiceImpl implements SessionInformationExpiredStrategyService {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        ResponseUtils.result(event.getResponse(), ResponseResult.builder().error("你的账号在另一地点被登录").build());
    }
}
