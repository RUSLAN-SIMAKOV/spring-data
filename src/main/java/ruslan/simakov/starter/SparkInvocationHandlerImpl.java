package ruslan.simakov.starter;

import lombok.Builder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.context.ConfigurableApplicationContext;
import scala.Tuple2;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Builder
public class SparkInvocationHandlerImpl implements SparkInvocationHandler {

    private Class<?> modelClass;
    private String pathToData;
    private DataExtractor dataExtractor;
    private Map<Method,List<Tuple2<SparkTransformation, List<String>>>> transformationChain;
    private Map<Method,Finalizer> finalizerMap;
    private FinalizerPostProcessor finalizerPostProcessor;
    private ConfigurableApplicationContext ctx;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> argsList = Arrays.asList(args);
        Dataset<Row> rowDataset = dataExtractor.readData(pathToData, ctx);
        List<Tuple2<SparkTransformation, List<String>>> tuple2s = transformationChain.get(method);
        for (Tuple2<SparkTransformation, List<String>> tuple : tuple2s) {
            SparkTransformation sparkTransformation = tuple._1();
            List<String> fieldNames = tuple._2();
            rowDataset = sparkTransformation.transform(rowDataset, fieldNames, argsList);
        }
        Finalizer finalizer = finalizerMap.get(method);
        Object retVal = finalizer.doAction(rowDataset, modelClass);
        return finalizerPostProcessor.postFinalize(retVal);
    }
}
