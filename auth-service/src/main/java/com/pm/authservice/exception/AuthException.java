package com.pm.authservice.exception;

public class AuthException extends RepException{

    private static final long serialVersionUID = 1325428338103933231L;

    /**
     * Default constructor.
     *
     * @see RepException#RepException()
     */
    public AuthException() {
        super();
    }

    /**
     * Constructor with messageKey parameter.
     *
     * @param msgKey
     *            <code>String</code>
     *            The messageKey to set as exception message.
     * @see RepException#RepException(String)
     */
    public AuthException(String msgKey) {
        super(msgKey);
    }

    /**
     * Constructor with messageKey and <code>Throwable</code> parameter.
     *
     * @param msgKey
     *            <code>String</code>
     *            The messageKey to set as exception message.
     * @param t
     *            <code>Throwable</code>
     *            The throwable instance.
     * @see RepException#RepException(String, Throwable)
     */
    public AuthException(String msgKey, Throwable t) {
        super(msgKey, t);
    }

    /**
     * Constructor with messageKey and <code>String</code>[] parameter.
     *
     * @param msgKey
     *            <code>String</code>
     *            The messageKey to set as exception message.
     * @param args
     *            <code>String</code>[]
     *            instance.
     * @see RepException#RepException(String, String[])
     */
    public AuthException(String msgKey, String[] args) {
        super(msgKey, args);
    }


}
