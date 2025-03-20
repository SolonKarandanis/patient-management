package com.pm.authservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application-speccific exception class. Immediate successor of
 * <code>RepException</code>. This Exception should be used as the end point for all 'expected' exceptions
 * ( i.e Exceptions that should be handled more gracefully )
 * <br/>
 * For 'unexpected' exceptions use <code>EDException</code>
 *
 * @author skaran
 */
public class BusinessException extends RepException {

    private static final long serialVersionUID = -924235550665734681L;

    /**
     * Default constructor.
     *
     * @see RepException#RepException()
     */
    public BusinessException() {
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
    public BusinessException(String msgKey) {
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
    public BusinessException(String msgKey, Throwable t) {
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
    public BusinessException(String msgKey, String[] args) {
        super(msgKey, args);
    }

    /**
     * log at DEBUG level all Business Exceptions based on the first level of the stacktrace
     * otherwise the reporting class will be the initiating RepException
     */
    @Override
    protected void logMe() {
        Class<?> causeClass = this.getInitRepException().getClass();
        Logger logger = LoggerFactory.getLogger(causeClass);
        logger.debug(causeClass.getName(), getMessage(), getCause());
    }

} 
