package com.pm.notificationservice.shared.exception;

/**
 * Application-speccific exception class. Immediate successor of
 * <code>RepException</code>. This Exception should be used as the end point for all unexpected exceptions
 * <br/>
 * For business specific exceptions that are 'expected' use the <code>BusinessException</code>
 *
 * @author solon
 */

public class NotificationServiceException extends RepException{

	private static final long serialVersionUID = 1325428338103933231L;
	
	/**
     * Default constructor.
     *
     * @see RepException#RepException()
     */
    public NotificationServiceException() {
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
    public NotificationServiceException(String msgKey) {
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
    public NotificationServiceException(String msgKey, Throwable t) {
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
    public NotificationServiceException(String msgKey, String[] args) {
        super(msgKey, args);
    }


}
