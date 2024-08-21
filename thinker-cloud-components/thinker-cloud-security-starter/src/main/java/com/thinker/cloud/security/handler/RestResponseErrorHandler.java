package com.thinker.cloud.security.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

/**
 * 重写默认的 响应失败处理器，400 不作为异常
 *
 * @author admin
 */
public class RestResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (!HttpStatus.BAD_REQUEST.equals(response.getStatusCode())) {
            super.handleError(response);
        }
    }

}
