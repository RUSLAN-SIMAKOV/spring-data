package starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataExtractorResolver {

    @Autowired
    private Map<String , DataExtractor> dataExtractorMap;

    public DataExtractor resolve(String pathToData) {
        String fileExt = pathToData.split("\\.")[1];
        return dataExtractorMap.get(fileExt);
    }
}
