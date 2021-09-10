package starter;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import ruslan.simakov.Source;
import ruslan.simakov.SparkRepository;
import scala.Tuple2;

import java.beans.Introspector;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

@Component
@RequiredArgsConstructor
public class SparkInvocationHandlerFactory {

    private final DataExtractorResolver dataResolver;
    private final Map<String, TransformationSpider> transformationSpiderMap;
    private final Map<Method, Finalizer> finalizerMap;

    @Setter
    private ConfigurableApplicationContext realCtx;

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

        Map<Method, List<Tuple2<SparkTransformation, List<String>>>> transformationChain = new HashMap<>();
        Map<Method, Finalizer> method2Finalizer = new HashMap<>();

        Method[] methods = repoInterface.getMethods();
        for (Method method : methods) {
            TransformationSpider currentSpider = null;
            List<Tuple2<SparkTransformation, List<String>>> transformations = new ArrayList<>();
            List<String> methodWords = new ArrayList<>(asList(method.getName().split("(?=\\p{Upper})")));
            while (methodWords.size() > 1) {
                String strategyName = WordsMatcher.findAndRemoveMatchingPiecesIfExist(transformationSpiderMap.keySet(), methodWords);
                if (!strategyName.isEmpty()) {
                    currentSpider = transformationSpiderMap.get(strategyName);
                }
                transformations.add(currentSpider.createTransformation(methodWords, fieldsName));

            }

            transformationChain.put(method, transformations);

            String finalizerName = "collect";
            if (methodWords.size() == 1) {
                finalizerName = Introspector.decapitalize(methodWords.get(0));
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
                .ctx(realCtx)
                .build();
        return sparkInvocationHandler;
    }
}
