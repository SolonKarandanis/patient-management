package com.pm.authservice.exception;

import com.pm.authservice.util.MiscUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Entry point exception, with localized messages and unique id generation
 *
 * @author solon
 */
public class RepException extends Exception{

	private static final long serialVersionUID = -8384938002488510975L;
	
	protected String id;
    protected String messageKey;

    private String[] keyArgs;
    
    public RepException() {
        super();
        id = MiscUtil.generateGUID(this);
        messageKey = "error.generic";
        logMe();
    }

    public RepException(String messageKey_) {
        super(messageKey_);
        id = MiscUtil.generateGUID(this);
        messageKey = messageKey_;
        logMe();
    }
    
    public RepException(String messageKey_, Throwable t) {
        super(messageKey_, t);
        messageKey = messageKey_;
        if (t instanceof RepException) {
            id = ((RepException) t).id;
        } else {
            id = MiscUtil.generateGUID(this);
        }
        logMe();
    }

    public RepException(String messageKey_, String[] args) {
        super(messageKey_);
        id = MiscUtil.generateGUID(this);
        messageKey = messageKey_;
        keyArgs = args;
        logMe();
    }

    public String getId() {
        return id;
    }

    public RepException getInitRepException() {
        Throwable inner1 = this.getCause();
        Throwable inner2 = this;
        while (inner1 != null && inner1 instanceof RepException) {
            inner2 = inner1;
            inner1 = inner1.getCause();
        }
        return (RepException) inner2;
    }
    
    public Throwable getInitCheckedException() {
        return getInitRepException().getCause();
    }

    public String getInitRepExceptionMessage() {
        return getInitRepException().getLocalizedMessage();
    }

    public String getInitCheckedExceptionMessage() {
        return getInitCheckedException().getLocalizedMessage();
    }

    @Override
    public String getMessage() {
        return "id " + getId() + ", " + super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return messageKey;
    }

    public String[] getKeyArgs() {
        return keyArgs;
    }

    /**
     * log at ERROR level all Rep Exceptions ( ED, Business etc ) based on the first level of the stacktrace
     * otherwise the reporting class will be the initiating RepException ( UNLESS OVERRIDEN PER CLASS )
     */
    protected void logMe() {
        Class<?> causeClass = this.getInitRepException().getClass();
        Logger logger = LoggerFactory.getLogger(causeClass);
        logger.error(causeClass.getName(), getMessage(), getCause());
    }

}
