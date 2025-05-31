package mdmihassan.web.api;

public class ResourceDeleteFailedException extends RuntimeException {
    public ResourceDeleteFailedException(String message) {
        super(message);
    }
}
