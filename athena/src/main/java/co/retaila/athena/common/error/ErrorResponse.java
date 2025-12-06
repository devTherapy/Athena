package co.retaila.athena.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private String error;
    private String errorDescription;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public String getTimestamp() {
        return this.timestamp.toString();
    }

}
