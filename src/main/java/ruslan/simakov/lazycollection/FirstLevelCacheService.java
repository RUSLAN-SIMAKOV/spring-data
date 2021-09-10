package ruslan.simakov.lazycollection;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import ruslan.simakov.starter.DataExtractor;
import ruslan.simakov.starter.DataExtractorResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstLevelCacheService {

    private Map<Class<?>, Dataset<Row>> model2Dataset = new HashMap<>();

    @Autowired
    private DataExtractorResolver resolver;

    public List getDataFor(long id, Class<?> model, String path, String foreignKey, ConfigurableApplicationContext ctx) {
        if(!model2Dataset.containsKey(model)) {
            DataExtractor dataExtractor = resolver.resolve(path);
            Dataset<Row> rowDataset = dataExtractor.readData(path, ctx).persist();
            model2Dataset.put(model, rowDataset);
        }
        return model2Dataset.get(model).filter(functions.col(foreignKey).equalTo(id)).as(Encoders.bean(model)).collectAsList();
    }
}
