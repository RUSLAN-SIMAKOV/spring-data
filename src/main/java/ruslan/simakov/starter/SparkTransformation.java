package ruslan.simakov.starter;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import java.util.List;

public interface SparkTransformation {
    Dataset<Row> transform(Dataset<Row> rowDataset, List<String> fieldNames, List<Object> args);
}
