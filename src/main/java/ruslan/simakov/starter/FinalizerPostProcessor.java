package ruslan.simakov.starter;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import ruslan.simakov.ForeignKeyName;
import ruslan.simakov.Source;
import ruslan.simakov.lazycollection.LazySparkList;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class FinalizerPostProcessor {

    private final ConfigurableApplicationContext context;

    @SneakyThrows
    Object postFinalize(Object retVal) {
        if (Collection.class.isAssignableFrom(retVal.getClass())) {
            List list = (List) retVal;
            for (Object model : list) {
                Field idField = model.getClass().getDeclaredField("id");
                idField.setAccessible(true);
                long ownerId = idField.getLong(model);


                Field[] declaredFields = model.getClass().getDeclaredFields();
                for (Field declaredField : declaredFields) {
                    if (List.class.isAssignableFrom(declaredField.getType())) {
                        LazySparkList lazySparkList = context.getBean(LazySparkList.class);
                        lazySparkList.setOwnerId(ownerId);
                        String columnName = declaredField.getAnnotation(ForeignKeyName.class).value();
                        lazySparkList.setForeignKeyName(columnName);
                        ParameterizedType genericType = (ParameterizedType) declaredField.getGenericType();
                        Class<?> embeddedModel = (Class<?>) genericType.getActualTypeArguments()[0];
                        lazySparkList.setModelClass(embeddedModel);
                        String pathToData = embeddedModel.getAnnotation(Source.class).value();
                        lazySparkList.setPathToSource(pathToData);

                        declaredField.setAccessible(true);
                        declaredField.set(model, lazySparkList);

                    }
                }

            }
        }
        return retVal;
    }
}
