package com.imooc.exception;

import com.imooc.result.CodeMsg;
import com.imooc.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.security.auth.callback.CallbackHandler;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(CallbackHandler.class);

    @ExceptionHandler(value = Exception.class)
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e) {
        if (e instanceof GlobalException) {  // 处理业务异常
            GlobalException ex = (GlobalException) e;
            return Result.error(ex.getCodeMsg());
        } else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            List<ObjectError> errors = ex.getAllErrors();
            ObjectError error = errors.get(0);
            String msg = error.getDefaultMessage();

            return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
        } else {
            log.error("Exception的类型为：" + e.getClass());
            e.printStackTrace();
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }

}
