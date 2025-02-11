package vn.minhhai.springb_fskill.dto.response;

@SuppressWarnings("rawtypes")
public class ResponseError extends ResponseData {

    public ResponseError(int status, String message) {
        super(status, message);
    }
}
