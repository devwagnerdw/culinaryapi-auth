package CulinaryAPI_app.exception;

public class HttpClientException extends RuntimeException{
    public HttpClientException(String message) {
        super(message);
    }
}