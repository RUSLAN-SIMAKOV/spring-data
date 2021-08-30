package starter;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.reflections.Reflections;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import ruslan.simakov.SparkRepository;

import java.beans.Introspector;
import java.lang.reflect.Proxy;

public class SparkDataApplicationContextInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext context) {
        registerSparkBeans(context);
        scanPackagesAndSearchSuccessorsOfSparkRepository(context);
    }

    private void registerSparkBeans(ConfigurableApplicationContext context) {
        SparkSession sparkSession = SparkSession.builder().master("local[*]")
                .appName(context.getEnvironment().getProperty("spark.app-name"))
                .getOrCreate();
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkSession.sparkContext());
        context.getBeanFactory().registerSingleton("sparkSession", sparkSession);
        context.getBeanFactory().registerSingleton("sparkContext", javaSparkContext);
    }

    private void scanPackagesAndSearchSuccessorsOfSparkRepository(ConfigurableApplicationContext context) {
        Reflections scanner = new Reflections(context.getEnvironment().getProperty("spark.packages-to-scan"));
        scanner.getSubTypesOf(SparkRepository.class).forEach(sparkRepositoryInterface -> {
            Object kreng = Proxy.newProxyInstance(
                    sparkRepositoryInterface.getClassLoader(),
                    new Class[]{sparkRepositoryInterface},
                    ih);
            context.getBeanFactory().registerSingleton(
                    Introspector.decapitalize(sparkRepositoryInterface.getSimpleName()), kreng);
        });
    }
}
