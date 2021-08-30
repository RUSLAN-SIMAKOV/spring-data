package starter;

import java.util.Map;

public class DataExtractorResolver {
    private Map<String , DataExtractor> dataExtractorMap;

    public DataExtractor resolve(String pathToData) {
        String fileExt = pathToData.split("\\.")[1];
        return dataExtractorMap.get(fileExt);
    }
}
