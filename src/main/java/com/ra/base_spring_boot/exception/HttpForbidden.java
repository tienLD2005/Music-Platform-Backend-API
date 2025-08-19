package com.ra.base_spring_boot.exception;

public class HttpForbidden extends RuntimeException
{
    public HttpForbidden(String message)
    {
        super(message);
    }
}
