package ruslan.simakov.lazycollection;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@Aspect
public class LazySparkListSupportAspect {

    @Autowired
    private FirstLevelCacheService cacheService;

    @Autowired
    private ConfigurableApplicationContext context;

    @Before("execution(* ruslan.simakov.lazycollection.LazySparkList.*(..)) && execution(* java.util.*.*(..))")
    public void beforeEachMethodInvocationCheckAndFillContent(JoinPoint jp) {
        LazySparkList lazySparkList = (LazySparkList) jp.getTarget();
        if (!lazySparkList.initialized()) {
            List dataFor = cacheService.getDataFor(lazySparkList.getOwnerId(),
                    lazySparkList.getModelClass(),
                    lazySparkList.getPathToSource(),
                    lazySparkList.getForeignKeyName(),
                    context);
            lazySparkList.setList(dataFor);
        }
    }
}
