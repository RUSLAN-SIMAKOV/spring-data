package starter;

import java.lang.reflect.Method;
import java.util.List;

public class SparkInvocationHandlerImpl implements SparkInvocationHandler {

    private Class<?> modelClass;
    private String pathToData;
    private DataExtractor dataExtractor;
    private List<SparkTransformation> transformationChain;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
