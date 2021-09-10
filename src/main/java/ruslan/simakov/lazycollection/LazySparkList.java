package ruslan.simakov.lazycollection;

import lombok.Data;
import lombok.experimental.Delegate;

import java.util.List;

@Data
public class LazySparkList implements List {

    @Delegate
    private List list;

    private long ownerId;
    private Class<?> modelClass;
    private String foreignKeyName;
    private String pathToSource;

    public boolean initialized() {
        return !(list == null || list.isEmpty());
    }
}
