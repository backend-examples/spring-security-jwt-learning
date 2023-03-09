package com.security.service;

import com.security.model.ClientDetailForm;
import com.security.pojo.ClientDetail;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface ClientService extends ClientDetailsService {
    String addClient(ClientDetailForm clientDetailForm) throws Exception;

    ClientDetail getClient(String clientId);
}
