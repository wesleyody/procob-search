package br.com.procob.search.model;

import java.util.List;

public class Response {

    private String code;

    private String message;

    private List< ResponseContent > content;

    public String getCode () {
        return code;
    }

    public void setCode ( String code ) {
        this.code = code;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage ( String message ) {
        this.message = message;
    }

    public List< ResponseContent > getContent () {
        return content;
    }

    public void setContent ( List< ResponseContent > content ) {
        this.content = content;
    }
}
