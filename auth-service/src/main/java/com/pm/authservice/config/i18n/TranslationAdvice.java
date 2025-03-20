package com.pm.authservice.config.i18n;


import com.pm.authservice.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.*;

@ControllerAdvice
public class TranslationAdvice implements ResponseBodyAdvice<Object> {
	
	 protected static Logger log = LoggerFactory.getLogger(TranslationAdvice.class);

    @Autowired
    MessageSource messageSource;

    private Locale locale;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		 logWarningIfMethodIsNull(returnType);
	        Method ofReturnTypeFunct = returnType.getMethod();
	        if (ofReturnTypeFunct == null) {
	            return false;
	        }
	        return ofReturnTypeFunct.isAnnotationPresent(Translate.class)
	                || ofReturnTypeFunct.isAnnotationPresent(Translate.Translations.class);
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
			ServerHttpResponse response) {
		Method ofReturnTypeFunct = returnType.getMethod();
        Translate[] translations = ofReturnTypeFunct != null ? ofReturnTypeFunct.getAnnotationsByType(Translate.class)
                : new Translate[] {};

        Object retVal;
        if (body instanceof ResponseEntity) {
            retVal = ((ResponseEntity<?>) body).getBody();
        } else {
            retVal = body;
        }

        this.locale = getRequestLocale(request);

        for (int i = 0, n = translations.length; i < n; i++) {
            Translate translate = translations[i];
            processPath(retVal, translate.path(), translate.targetProperty());
        }

        return body;
	}
	
	  /**
     * @param request
     *            <code>WebRequest</code>
     * @return
     */
    private Locale getRequestLocale(final ServerHttpRequest request) {
        String langIsoCode = getLanguage(request);
        return (StringUtils.hasLength(langIsoCode)) ? new Locale(langIsoCode) : Locale.ENGLISH;
    }

    protected String getLanguage(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        return headers.getFirst(AppConstants.HEADER_NAME_LANGUAGE_ISO);
    }
    
    protected void processPath(Object bean, String fullpath, String targetPropertyName) {

        if (bean == null) {
            return;
        }

        int index = fullpath.indexOf('.');
        String path1;
        String path2 = null;
        if (index > 0) {
            path1 = fullpath.substring(0, index);
            path2 = fullpath.substring(index + 1);
        } else {
            path1 = fullpath;
        }

        if (path2 == null) {
            processPropertyValue(bean, path1, targetPropertyName, true);
            // return, stop the recursion
        } else {
            List<Object> propertyValue = processPropertyValue(bean, path1, targetPropertyName, false);
            for (int i = 0, n = propertyValue.size(); i < n; i++) {
                processPath(propertyValue.get(i), path2, targetPropertyName);
            }
        }

    }
    
    protected List<Object> processPropertyValue(Object bean, String propertyName, String targetPropertyName,
            boolean isLeaf) {
        List<Object> retVal;
        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
        if (propertyName.endsWith("[*]")) {
            retVal = processMulti(bean, propertyName, isLeaf, beanWrapper);
        } else {
            retVal = processSingle(propertyName, targetPropertyName, isLeaf, beanWrapper);
        }

        return isLeaf ? null : retVal;
    }
    
    private List<Object> processMulti(Object bean, String propertyName, boolean isLeaf, BeanWrapper beanWrapper) {

        List<Object> retVal = new ArrayList<>(64);
        int index = propertyName.indexOf("[*]");

        Object propertyValue;
        if (index == 0) {
            // e.g. the propertyName is [*] i.e. no property name and in fact
            //bean it self is Array or Collection
            propertyValue = bean;
        } else {
            // e.g. the propertyName is prop[*]; i.e. we have the name of the property
            // and it is Array or Collection
            propertyValue = beanWrapper.getPropertyValue(propertyName.substring(0, index));
        }

        if (propertyValue != null) {
            processPropertyValue(propertyValue, isLeaf, retVal);
        }
        return retVal;
    }
    
    private void processPropertyValue(Object propertyValue, boolean isLeaf, List<Object> retVal) {

        if (propertyValue.getClass().isArray()) {
            processArray(isLeaf, retVal, propertyValue);

        } else {
            if (propertyValue instanceof Iterable) {
                processIterable(isLeaf, retVal, propertyValue);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void processIterable(boolean isLeaf, List<Object> retVal, Object propertyValue) {
        Iterable<?> iter = (Iterable<?>) propertyValue;
        if (isLeaf) {
            // we assume that mosts of our DTOs will have List of
            // Strings
            // However the following will fail if we have Map that
            // we want to translate either keys or values
            ListIterator<String> listIter = (ListIterator<String>) iter.iterator();
            String elem;
            while (listIter.hasNext()) {
                elem = listIter.next();
                listIter.set(translate(elem));
            }

        } else {
            for (Object item : iter) {
                retVal.add(item);
            }
        }
    }
    
    private void processArray(boolean isLeaf, List<Object> retVal, Object propertyValue) {
        Object[] arr = (Object[]) propertyValue;
        if (isLeaf) {
            for (int i = 0, n = arr.length; i < n; i++) {
                arr[i] = translate((String) arr[i]);
            }
        } else {
            for (int i = 0, n = arr.length; i < n; i++) {
                retVal.add(arr[i]);
            }
        }
    }


    private List<Object> processSingle(String propertyName, String targetPropertyName, boolean isLeaf,
            BeanWrapper beanWrapper) {

        List<Object> retVal;
        Object propertyValue = beanWrapper.getPropertyValue(propertyName);
        if (isLeaf && Objects.nonNull(propertyValue)) {
            String translatedValue = translate((String) propertyValue);
            if (targetPropertyName == null || "".equals(targetPropertyName)) {
                beanWrapper.setPropertyValue(propertyName, translatedValue);
            } else {
                beanWrapper.setPropertyValue(targetPropertyName, translatedValue);
            }

            retVal = null;
        } else {
            retVal = new ArrayList<>(1);
            retVal.add(propertyValue);
        }
        return retVal;
    }
    
    /**
     *
     */
    private String translate(final String key) {
        return Optional.ofNullable(messageSource.getMessage(key, null, locale)).orElse(key);
    }
    
    /**
     * @param returnType
     *            <code>MethodParameter</code>
     */
    private void logWarningIfMethodIsNull(final MethodParameter returnType) {
        if (returnType.getMethod() == null) {
            log.warn(" Warning: returnType.getMethod() returns NULL, Parameter[Name: {}, Type: {}, Index: {}] ",
                    returnType.getParameterName(), returnType.getParameterType(), returnType.getParameterIndex());
        }
    }


}
