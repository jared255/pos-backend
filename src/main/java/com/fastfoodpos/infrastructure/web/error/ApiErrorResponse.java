package com.fastfoodpos.infrastructure.web.error;

import java.util.List;

public class ApiErrorResponse {
    private String message;
    private List<String> details;

    public ApiErrorResponse(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}
