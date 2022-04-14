package com.imooc.exception;

import com.imooc.result.CodeMsg;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException{


    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

}
