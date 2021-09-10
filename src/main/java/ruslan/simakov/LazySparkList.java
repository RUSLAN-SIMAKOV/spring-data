package ruslan.simakov;

import lombok.experimental.Delegate;

import java.util.List;

public class LazySparkList implements List {

    @Delegate
    private List list;
}
