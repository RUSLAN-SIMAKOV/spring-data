package starter;

import java.util.List;

public interface TransformationSpider {

    SparkTransformation createTransformation(List<String> remainingWords);
}
