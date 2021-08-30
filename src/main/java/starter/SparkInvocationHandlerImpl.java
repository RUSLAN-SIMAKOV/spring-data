package starter;

import lombok.Builder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Builder
public class SparkInvocationHandlerImpl implements SparkInvocationHandler {

    private Class<?> modelClass;
    private String pathToData;
    private DataExtractor dataExtractor;
    private Map<Method,List<SparkTransformation>> transformationChain;
    private Map<Method,Finalizer> finalizerMap;
    private ConfigurableApplicationContext ctx;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Dataset<Row> rowDataset = dataExtractor.readData(pathToData, ctx);
        List<SparkTransformation> sparkTransformations = transformationChain.get(method);
        for (SparkTransformation transformation: sparkTransformations) {
            rowDataset = transformation.transform(rowDataset);
        }
        Finalizer finalizer = finalizerMap.get(method);
        Object retVal = finalizer.doAction(rowDataset);
        return retVal;
    }
}
