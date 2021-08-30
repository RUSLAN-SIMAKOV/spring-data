package starter;

import org.springframework.context.ConfigurableApplicationContext;
import ruslan.simakov.Source;
import ruslan.simakov.SparkRepository;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class SparkInvocationHandlerFactory {

    private DataExtractorResolver dataResolver;
    private Map<String, TransformationSpider> transformationSpiderMap;
    private Map<Method, Finalizer> finalizerMap;
    private ConfigurableApplicationContext ctx;

    public SparkInvocationHandler create(Class<? extends SparkRepository> repoInterface) {
        ParameterizedType genericInterface = (ParameterizedType) repoInterface.getGenericInterfaces()[0];
        Class<?> modelClass = (Class<?>) genericInterface.getActualTypeArguments()[0];
        Set<String> fieldsName = Arrays.stream(modelClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .filter(field -> !Collection.class.isAssignableFrom(field.getType()))
                .map(Field::getName)
                .collect(Collectors.toSet());
        String pathToData = modelClass.getAnnotation(Source.class).value();
        DataExtractor dataExtractor = dataResolver.resolve(pathToData);

        Map<Method, List<SparkTransformation>> transformationChain = new HashMap<>();
        Map<Method, Finalizer> method2Finalizer = new HashMap<>();

        Method[] methods = repoInterface.getMethods();
        for (Method method : methods) {
            TransformationSpider currentSpider = null;
            List<SparkTransformation> transformations = new ArrayList<>();
            List<String> methodWords = new ArrayList<>(asList(method.getName().split("(?=\\p{Upper})")));
            while (methodWords.size() > 1) {
                String strategyName = WordsMatcher.findAndRemoveMatchingPiecesIfExist(transformationSpiderMap.keySet(), methodWords);
                if (!strategyName.isEmpty()) {
                    currentSpider = transformationSpiderMap.get(strategyName);
                }
                transformations.add(currentSpider.createTransformation(methodWords));

            }

            transformationChain.put(method, transformations);

            String finalizerName = "collect";
            if (methodWords.size() == 1) {
                finalizerName = methodWords.get(0);
            }
            Finalizer finalizer = finalizerMap.get(finalizerName);
            method2Finalizer.put(method, finalizer);

        }

        SparkInvocationHandler sparkInvocationHandler = SparkInvocationHandlerImpl.builder()
                .modelClass(modelClass)
                .transformationChain(transformationChain)
                .finalizerMap(method2Finalizer)
                .dataExtractor(dataExtractor)
                .pathToData(pathToData)
                .ctx(ctx)
                .build();
        return sparkInvocationHandler;
    }
}
