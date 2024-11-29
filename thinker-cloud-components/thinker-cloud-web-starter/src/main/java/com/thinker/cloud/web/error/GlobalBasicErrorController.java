package com.thinker.cloud.web.error;

import com.thinker.cloud.common.enums.ResponseCode;
import com.thinker.cloud.core.model.Result;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 全局避免重定向到 /error 导致NPE
 *
 * @author admin
 **/
@Hidden
@RestController
@RequestMapping("${server.error.path:${error.path:/error}}")
public class GlobalBasicErrorController implements ErrorController {

    @RequestMapping(method = RequestMethod.POST)
    public Result<Void> error(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        ResponseCode responseCode = switch (status) {
            case OK -> ResponseCode.FAILURE;
            case BAD_REQUEST -> ResponseCode.BAD_REQUEST;
            case UNAUTHORIZED -> ResponseCode.UNAUTHORIZED;
            case NOT_FOUND -> ResponseCode.NOT_FOUND;
            case SERVICE_UNAVAILABLE -> ResponseCode.UNAVAILABLE;
            default -> ResponseCode.SERVER_FAILURE;
        };
        return Result.failure(responseCode);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
