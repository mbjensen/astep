package dk.aau.astep.appserver.restapi.api;

import dk.aau.astep.appserver.model.outdoor.JsonResponseMatchRoute;
import dk.aau.astep.appserver.model.shared.JsonResponse;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.Duration;
import java.time.Instant;

/**
 * Created by Morten on 06/03/2016.
 */


@XmlRootElement
public class ApiResponseMessage {

    private JsonResponse body;
    private JsonResponseMatchRoute data;
    private java.time.Duration update_scheme;
    private String error_message;

    public ApiResponseMessage() {}

    public ApiResponseMessage(JsonResponse body) {
        this.body = body;
    }

    public ApiResponseMessage(JsonResponseMatchRoute data) {
        this.data = data;
    }

    public ApiResponseMessage(java.time.Duration update_scheme) {
        this.update_scheme = update_scheme;
    }

    public ApiResponseMessage(String errorInfo) {this.error_message = errorInfo;}

    public JsonResponse getBody() {
        return this.body;
    }

    public void setBody(JsonResponse body) {
        this.body = body;
    }

    public JsonResponseMatchRoute getData() {
        return this.data;
    }

    public void setData(JsonResponseMatchRoute data) {
        this.data = data;
    }

    public java.time.Duration getUpdate_scheme() {
        return this.update_scheme;
    }

    public void setUpdate_scheme(java.time.Duration update_scheme) {
        this.update_scheme = update_scheme;
    }

    public String getError_message() {
        return this.error_message;
    }

    public void setError_message(String errorInfo) {
        this.error_message = errorInfo;
    }
}
