package fi.hut.soberit.agilefant.util;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

@Aspect
public class TransactionLoggerAspect {

    private final Logger log = Logger.getLogger(this.getClass());

    private final Map<TransactionStatus, WeakReference<TransactionDefinition>> txCache = Collections
            .synchronizedMap(new WeakHashMap<TransactionStatus, WeakReference<TransactionDefinition>>());

    private final TxCounter txCounter = new TxCounter();

    class TxCounter extends ThreadLocal<Integer> {

        public String toString() {
            return this.get().toString();
        }

        public Integer initialValue() {
            return 0;
        }

        public void increment() {
            this.set(this.get() + 1);
        }

        public void decrement() {
            this.set(this.get() - 1);
        }

    }

    @Around("bean(transactionManager) && execution(public * getTransaction(..))")
    public Object getTransaction(ProceedingJoinPoint pjp) throws Throwable {
        TransactionDefinition td = (TransactionDefinition) pjp.getArgs()[0];
        TransactionStatus ts = (TransactionStatus) pjp.proceed();
        txCounter.increment();
        if (log.isDebugEnabled()) {
            log.debug("getTransaction(#" + txCounter + ", new = "
                    + ts.isNewTransaction() + ", name = " + td.getName()
                    + ", readOnly = " + td.isReadOnly() + ", isolation = "
                    + td.getIsolationLevel() + ")");
        }
        txCache.put(ts, new WeakReference<TransactionDefinition>(td));
        return ts;
    }

    @Around("bean(transactionManager) && execution(public * commit(..))")
    public Object commit(ProceedingJoinPoint pjp) throws Throwable {
        TransactionStatus ts = (TransactionStatus) pjp.getArgs()[0];
        WeakReference<TransactionDefinition> tdRef = txCache.get(ts);
        TransactionDefinition td = (tdRef == null) ? null : tdRef.get();
        if (log.isDebugEnabled() && td != null) {
            log.debug("commit(#" + txCounter + ", new = "
                    + ts.isNewTransaction() + ", name = " + td.getName() + ")");
        }
        txCounter.decrement();
        txCache.remove(ts);
        return pjp.proceed();
    }

    @Around("bean(transactionManager) && execution(public * rollback(..))")
    public Object rollback(ProceedingJoinPoint pjp) throws Throwable {
        TransactionStatus ts = (TransactionStatus) pjp.getArgs()[0];
        WeakReference<TransactionDefinition> tdRef = txCache.get(ts);
        TransactionDefinition td = (tdRef == null) ? null : tdRef.get();
        if (log.isDebugEnabled() && td != null) {
            log.debug("rollback(#" + txCounter + ", new = "
                    + ts.isNewTransaction() + ", name = " + td.getName() + ")");
        }
        txCounter.decrement();
        txCache.remove(ts);
        return pjp.proceed();
    }

}