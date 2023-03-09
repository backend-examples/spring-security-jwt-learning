package com.security.service.impl;

import com.security.mapper.ClientDetailMapper;
import com.security.model.ClientDetailForm;
import com.security.pojo.ClientDetail;
import com.security.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientDetailMapper clientDetailMapper;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
//        ClientDetail clientDetail = clientDetailMapper.selectClientDetailByClientId(clientId);

        return null;
    }

    @Override
    public String addClient(ClientDetailForm clientDetailForm) throws Exception {
        ClientDetail clientDetail;

        clientDetail = clientDetailMapper.selectClientDetailByClientId(clientDetailForm.getClientId());

        if (clientDetail != null) {
            throw new Exception("clientId已存在");
        }

        clientDetail = clientDetailForm.toClientDetail(clientDetailForm);

        System.out.println("clientDetail: " + clientDetail);
//
//        String clientId = clientDetailMapper.insertClientDetail(clientDetail);

//        return clientId;
        return null;
    }

    @Override
    public ClientDetail getClient(String clientId) {
        return clientDetailMapper.selectClientDetailByClientId(clientId);
    }
}
