package vn.minhhai.springb_fskill.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Date;

import org.springframework.http.HttpStatus;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice // chú ý viết đúng RestControllerAdvice cho Restfull Api
public class GlobalExceptionHandler {

    @ExceptionHandler({ ConstraintViolationException.class, MethodArgumentNotValidException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(Exception e, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse();

        errorResponse.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();

        if (e instanceof MethodArgumentNotValidException) { // Ngoại lệ dữ liệu gửi về không đúng validate
            int start = message.lastIndexOf("[") + 1;
            int end = message.lastIndexOf("]") - 1;

            // cắt chuỗi để lấy message được thông báo ra bới exception
            message = message.substring(start, end);

            errorResponse.setError("Invalid Payload");
            errorResponse.setMessage(message);
        } else if (e instanceof ConstraintViolationException) { // Ngoại lệ Parameter không đúng validate
            errorResponse.setError("Invalid Parameter");
            errorResponse.setMessage(message);
        } else {
            errorResponse.setError("Invalid Data");
            errorResponse.setMessage(message);
        }

        return errorResponse;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorException(Exception e,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(new Date());
        errorResponse.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        if (e instanceof MethodArgumentTypeMismatchException) { // Ngoại lệ Parameter không đúng kiểu dữ liệu
            errorResponse.setMessage(e.getMessage());
        }

        return errorResponse;
    }
}
