package pl.matkmiec.backup.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/** DTO representing an error response.
 * Returned when an error occurs during the processing of a request.*/
@Data
@AllArgsConstructor
@Builder
public
class ErrorResponseDto {
    /** The HTTP status code of the error. */
    private int status;

    /** The error message. */
    private String message;

    /** Additional details about the error. */
    private String details;

    /** The path of the request that caused the error. */
    private String path;

    /** The timestamp of when the error occurred. */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    /** Constructor without timestamp, set to current time. */
    public ErrorResponseDto(int status, String message, String details, String path) {
        this.status = status;
        this.message = message;
        this.details = details;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
