package starter;

import scala.Tuple2;

import java.util.List;
import java.util.Set;

public interface TransformationSpider {

    Tuple2<SparkTransformation, List<String>> createTransformation(List<String> remainingWords, Set<String> fieldNames);
}
