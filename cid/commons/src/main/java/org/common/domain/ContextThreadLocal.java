package org.common.domain;

/**
 * The Class ContextThreadLocal can be used as object store for the running thread.
 * The objects stored in this thread can be accessed anytime within the lifetime of the thread.
 * Once thread execution is completed GC will clear the objects from this store.
 */
public final class ContextThreadLocal {

	private static final ThreadLocal<ContextInfo> THREAD_LOCAL_CONTEXT_INFO = new ThreadLocal<ContextInfo>();

	private ContextThreadLocal() {
	}

	public static void set(final ContextInfo contextInfo) {
		THREAD_LOCAL_CONTEXT_INFO.set(contextInfo);
	}

	public static ContextInfo get() {
		return THREAD_LOCAL_CONTEXT_INFO.get();
	}

	public static void unset() {
		THREAD_LOCAL_CONTEXT_INFO.remove();
	}
}