package com.example.demo.config;

import java.io.Serializable;

//Csrfの取得に使用
public interface CsrfToken extends Serializable {
    String getHeaderName();
    String getParameterName();
    String getToken();
}