package ruslan.simakov;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Source("/data/criminals.csv")
public class Criminal {

    private long id;
    private String name;
    private int number;
}
