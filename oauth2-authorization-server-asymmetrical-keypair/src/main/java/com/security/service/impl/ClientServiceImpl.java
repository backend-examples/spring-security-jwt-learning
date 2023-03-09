package com.security.service.impl;

import com.security.mapper.ClientDetailMapper;
import com.security.pojo.ClientDetail;
import com.security.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientDetailMapper clientDetailMapper;

    @Override
    public String addClient(ClientDetail clientDetail) {

        clientDetailMapper.insertClientDetail(clientDetail);

        log.info("已注册：" + clientDetail.getClientId());
        log.info("已注册密码：" + clientDetail.getPlainClientSecret());

        return clientDetail.getClientId();
    }
}
