package org.common.logging.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.common.constants.CommonConstants;
import org.common.domain.ContextInfo;
import org.common.domain.ContextThreadLocal;
import org.common.utils.LoggingUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * The Class LoggingInterceptor is an AOP implementation to convert string to ContextInfo,
 *  log method execution's START/END and log service's execution/performance time.
 *
 *  For regular expression please refer Spring AOP pointcut's documentation.
 */
@Aspect
@Component
public class LoggingInterceptor {

	private static final Logger LOGGER = LogManager.getLogger(LoggingInterceptor.class);

	/**
	 * Adds the context info to thread local by fetching it from request header.
	 * This method is executed on all the methods defined inside "controllers" package classes.
	 *
	 * @param joinPoint the join point specified by spring AOP
	 */
	//@Before("execution(* package.path..controllers..*.*(..))")
	@Before("execution(* uk.gov.nhs.digital.cid.admin..controllers..*.*(..))")
	public void addContextInfoToThreadLocal(final JoinPoint joinPoint) {
		ContextInfo contextInfo = getContextInfoFromMethodArguments(joinPoint);
		ContextThreadLocal.set(contextInfo);
	}

	/**
	 * Logs the method start for all the methods
	 * This method is executed on all the methods defined inside "package.path" package classes
	 * excluding the methods from orika package. Excluded this package because there are many internal
	 * method calls being made which are not necessary and they will fill up the log files rapidly.
	 *
	 * @param joinPoint the join point specified by spring AOP
	 */
	//@Before("execution(* package.path..*.*(..)) && !execution(* org.common.orika..*.*(..))")
	@Before("execution(* uk.gov.nhs.digital.cid.admin..*.*(..)) && !execution(* uk.gov.nhs.digital.cid.admin..*.*(..))")
	public void logMethodStart(final JoinPoint joinPoint) {
		String contextInfo = getContextInfo();
		String methodSignature = getMethodSignature(joinPoint).toString();
		LOGGER.debug(LoggingUtil.getMessageDescription(CommonConstants.LoggingPoint.START.toString(), new Object[] {methodSignature, contextInfo}));
	}

	/**
	 * Logs method end for all the methods
	 * This method is executed on all the methods defined inside "package.path" package classes
	 * excluding the methods from orika package. Excluded this package because there are many internal
	 * method calls being made which are not necessary and they will fill up the log files rapidly.
	 *
	 * @param joinPoint the join point specified by spring AOP
	 */
	//@AfterReturning("execution(* package.path..*.*(..)) && !execution(* org.common.orika..*.*(..))")
	@AfterReturning("execution(* uk.gov.nhs.digital.cid.admin..*.*(..)) && !execution(* uk.gov.nhs.digital.cid.admin.orika..*.*(..))")
	public void logMethodEnd(final JoinPoint joinPoint) {
		String contextInfo = getContextInfo();
		String methodSignature = getMethodSignature(joinPoint).toString();
		LOGGER.debug(LoggingUtil.getMessageDescription(CommonConstants.LoggingPoint.END.toString(), new Object[] {methodSignature, contextInfo}));
	}

	/**
	 * Gets the context info from "ContextThreadLocal" or returns EMPTY string if does not exists
	 *
	 * @return the context info in string format
	 */
	private String getContextInfo() {
		String contextInfoString = StringUtils.EMPTY;
		ContextInfo contextInfo = ContextThreadLocal.get();
		if(contextInfo != null && StringUtils.isNotEmpty(contextInfo.toString())) {
			contextInfoString = contextInfo.toString();
		}
		return contextInfoString;
	}

	/**
	 * This method measures the execution time for controller methods.
	 * This method is executed on all the methods defined inside "controllers" package classes.
	 *
	 * @param joinPoint the join point specified by spring AOP
	 * @return the return value (do not change the object state)
	 * @throws Throwable the throwable
	 */
	//@Around("execution(* package.path..controllers..*.*(..))")
	@Around("execution(* uk.gov.nhs.digital.cid.admin..controllers..*.*(..))")
	public Object logTimeMethod(final ProceedingJoinPoint joinPoint) throws Throwable {
		final StopWatch stopWatch = new StopWatch();

		stopWatch.start();
		final Object returnValue = joinPoint.proceed();
		stopWatch.stop();

		final StringBuilder logMessage = new StringBuilder(getMethodSignature(joinPoint));
		logMessage.append(" execution time: ");
		logMessage.append(stopWatch.getTotalTimeMillis());
		logMessage.append(" ms");
		LOGGER.debug("PERF_LOG=" + logMessage.toString());
		return returnValue;
	}

	/**
	 * Gets the method signature for logging
	 *
	 * @param joinPoint the join point specified by spring AOP
	 * @return the method signature
	 */
	private String getMethodSignature(final JoinPoint joinPoint) {
		final StringBuilder logMessage = new StringBuilder();
		logMessage.append(joinPoint.getTarget().getClass().getName());
		logMessage.append(".");
		logMessage.append(joinPoint.getSignature().getName());
		logMessage.append("(");

		final Object[] args = joinPoint.getArgs();
		for(final Object arg : args) {
			if(null != arg) {
				logMessage.append(arg.getClass().getSimpleName()).append(", ");
			}
		}

		if(args.length > 0) {
			logMessage.delete(logMessage.length() - 2, logMessage.length());
		}

		logMessage.append(")");
		return logMessage.toString();
	}

	/**
	 * Gets the context info from method arguments in string format and converts it to object.
	 *
	 * @param joinPoint the join point specified by spring AOP
	 * @return the context info from method arguments
	 */
	private ContextInfo getContextInfoFromMethodArguments(final JoinPoint joinPoint) {
		ContextInfo contextInfo = ContextInfo.toContextInfo(StringUtils.EMPTY);
		int contextInfoArgumentNumber = -1;
		int counter = 0;
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		final Annotation[][] annotations = method.getParameterAnnotations();
		for(final Annotation[] annotation : annotations) {
			if(null != annotation && annotation.length > 0 && null != annotation[0]) {
				String argumentSignature = annotation[0].toString();
				if(StringUtils.contains(argumentSignature, "org.springframework.web.bind.annotation.RequestHeader") && StringUtils.contains(argumentSignature, "value=" + CommonConstants.CONTEXT_INFORMATION_REQUEST_PARAMETER)) {
					contextInfoArgumentNumber = counter;
					break;
				}
			}
			counter++;
		}

		if(contextInfoArgumentNumber > -1) {
			contextInfo = ContextInfo.toContextInfo(joinPoint.getArgs()[contextInfoArgumentNumber].toString());
		}
		return contextInfo;
	}
}