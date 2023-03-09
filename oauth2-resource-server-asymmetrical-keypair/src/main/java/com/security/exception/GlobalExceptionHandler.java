package com.security.exception;

import com.security.constant.Messages;
import com.security.enums.CodeEnum;
import com.security.utils.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义全局异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义的业务异常
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public ResponseResult bizExceptionHandler(HttpServletRequest request, HttpServletResponse response, BizException e){
        logger.error("发生业务异常！原因是：" + e.getLocalizedMessage());

        response.setStatus(e.getErrorCode());

        return ResponseResult.builder().error(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理空指针的异常
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = NullPointerException.class)
    @ResponseBody
    public ResponseResult exceptionHandler(HttpServletRequest request, HttpServletResponse response, NullPointerException e){
        logger.error("发生空指针异常！原因是: " + e.getLocalizedMessage());

        response.setStatus(CodeEnum.BODY_NOT_MATCH.getCode());

        return ResponseResult.builder().error(Messages.NULL_EXCEPTION);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseResult requestInvalid(HttpServletRequest request, HttpServletResponse response, HttpMessageNotReadableException e) {
        logger.error("参数请求错误！原因是: " + e.getLocalizedMessage());

        response.setStatus(CodeEnum.BODY_NOT_MATCH.getCode());

        return ResponseResult.builder().error(CodeEnum.BODY_NOT_MATCH.getMessage());
    }

    /**
     * 参数校验的异常
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseResult exceptionHandler(HttpServletRequest request, HttpServletResponse response, MethodArgumentNotValidException e) {
        logger.error("参数校验异常！原因是: " + e.getLocalizedMessage());

        response.setStatus(CodeEnum.INTERNAL_SERVER_ERROR.getCode());

        return ResponseResult.builder().error(CodeEnum.INTERNAL_SERVER_ERROR.getCode(), e.getBindingResult().getFieldError().getDefaultMessage());
    }


    /**
     * 处理其他异常
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseResult exceptionHandler(HttpServletRequest req, HttpServletResponse response, Exception e){
        logger.error("未知异常！原因是: " + e.getLocalizedMessage());

        response.setStatus(CodeEnum.INTERNAL_SERVER_ERROR.getCode());

        return ResponseResult.builder().error(CodeEnum.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }
}
