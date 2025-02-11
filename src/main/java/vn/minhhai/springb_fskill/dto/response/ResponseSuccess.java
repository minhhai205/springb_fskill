package vn.minhhai.springb_fskill.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ResponseSuccess extends ResponseEntity<ResponseSuccess.Payload> {

    // PUT, PATCH, DELETE
    public ResponseSuccess(HttpStatus status, String message) {
        super(new Payload(status.value(), message), HttpStatus.OK);
    }

    // GET, POST
    public ResponseSuccess(HttpStatus status, String message, Object data) {
        super(new Payload(status.value(), message, data), HttpStatus.OK);
    }

    // public ResponseSuccess(Payload body, HttpStatus status) {
    // super(body, status);
    // }

    // public ResponseSuccess(MultiValueMap<String, String> headers, HttpStatus
    // status) {
    // super(headers, status);
    // }

    // public ResponseSuccess(Payload payload, MultiValueMap<String, String>
    // headers, int rawStatus) {
    // super(payload, headers, rawStatus);
    // }

    // public ResponseSuccess(Payload payload, MultiValueMap<String, String>
    // headers, HttpStatus status) {
    // super(payload, headers, status);
    // }

    public static class Payload {
        private final int status;
        private final String message;

        @JsonInclude(JsonInclude.Include.NON_NULL) // dadataa rỗng sẽ không hiển thị
        private Object data;

        public Payload(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public Payload(int status, String message, Object data) {
            this.status = status;
            this.message = message;
            this.data = data;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }
    }
}
