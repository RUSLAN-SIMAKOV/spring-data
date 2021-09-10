package ruslan.simakov.starter;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("between")
public class BetweenFilter implements FilterTransformation {
    @Override
    public Dataset<Row> transform(Dataset<Row> rowDataset, List<String> fieldNames, List<Object> args) {
        return rowDataset.filter(functions.col(fieldNames.get(0)).between(args.remove(0), args.remove(0)));
    }
}
