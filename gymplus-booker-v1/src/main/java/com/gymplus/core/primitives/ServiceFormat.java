package com.gymplus.core.primitives;

public enum ServiceFormat {
    JSON,
    URL,
    SOAP,
    XML,
    ASCII,
    TEXT,
    CSV,
    PDF,
    HTML
    ;

    public String getResponseContentType() {
        switch (this) {
            case SOAP: return "text/xml;charset=UTF-8";
            case JSON: return "application/json;charset=UTF-8";
            case URL: return "application/x-www-form-urlencoded;charset=UTF-8";
            case XML: return "application/xml;charset=UTF-8";
            case ASCII: return "application/xml;charset=US-ASCII";
            case TEXT: return "text/plain;charset=UTF-8";
            case CSV: return "text/plain;charset=UTF-8";
            case PDF: return "application/pdf";
            case HTML: return "text/html;charset=UTF-8";
            default: return "text/plain;charset=UTF-8";
        }
    }
}
