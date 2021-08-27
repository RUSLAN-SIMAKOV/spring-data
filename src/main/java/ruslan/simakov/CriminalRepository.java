package ruslan.simakov;

import ruslan.simakov.Criminal;

import java.util.List;

public interface CriminalRepository extends SparkRepository<Criminal> {

    List<Criminal> findByNumberBetween(int min, int max);
}
