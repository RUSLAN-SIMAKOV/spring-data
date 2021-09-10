package ruslan.simakov;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Source("data/orders.csv")
public class Order {

    private String name;
    private String desc;
    private int price;
    private long criminalId;
}
