package com.educode.apps.calculadoraaritmetica.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class EmailValidationResponse {

    @JsonProperty("format_valid")
    private boolean formatValid;

    @JsonProperty("mx_found")
    private boolean mxFound;

    private boolean disposable;

    public boolean isDisposable() {
        return disposable;
    }

    public void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }

    public boolean isFormatValid() {
        return formatValid;
    }

    public void setFormatValid(boolean formatValid) {
        this.formatValid = formatValid;
    }

    public boolean isMxFound() {
        return mxFound;
    }

    public void setMxFound(boolean mxFound) {
        this.mxFound = mxFound;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("disposable", disposable)
                .append("formatValid", formatValid)
                .append("mxFound", mxFound)
                .toString();
    }
}
