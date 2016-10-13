
package com.aton.base.secure;

public class SessionInvalidException extends RuntimeException {

    public int code;
    public String msg;
    public String session;

    public SessionInvalidException(int code) {
        this.code = code;
    }

    public SessionInvalidException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public SessionInvalidException(int code, String msg, String session) {
        this.code = code;
        this.msg = msg;
        this.session = session;
    }

    @Override
    public String toString() {
        return "ClientException [code=" + code + ", msg=" + msg + ", sessionKey=" + session + "]";
    }

}
