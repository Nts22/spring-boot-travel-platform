package com.ptirado.nmviajes.service;

import com.ptirado.nmviajes.dto.api.request.RegistroRequest;
import com.ptirado.nmviajes.dto.api.response.AuthResponse;

public interface AuthService {

    AuthResponse registrar(RegistroRequest request);

    boolean existeEmail(String email);
}
