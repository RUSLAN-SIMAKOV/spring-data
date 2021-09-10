package starter;

import lombok.SneakyThrows;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.springframework.stereotype.Component;

import javax.lang.model.type.ArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component("collect")
public class CollectFinalizerImpl implements Finalizer {
    @SneakyThrows
    @Override
    public Object doAction(Dataset<Row> rowDataset, Class<?> modelClass) {
        Encoder<?> encoder = Encoders.bean(modelClass);
        List<String> listFieldNames = Arrays.stream(encoder.schema().fields()).filter(structField -> structField.dataType() instanceof ArrayType)
                .map(StructField::name)
                .collect(Collectors.toList());
        for (String fieldName : listFieldNames) {
            ParameterizedType genericType = (ParameterizedType) modelClass.getDeclaredField(fieldName).getGenericType();
            Class c = (Class) genericType.getActualTypeArguments()[0];
            rowDataset.withColumn(fieldName, functions.lit(null).cast(DataTypes.createStructType(Encoders.bean(c).schema().fields())));
        }
        return rowDataset.as(encoder).collectAsList();
    }
}
