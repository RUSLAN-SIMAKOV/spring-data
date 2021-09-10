package ruslan.simakov;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Source("/data/criminals.csv")
public class Criminal {

    private long id;
    private String name;
    private int number;

    @ForeignKeyName("criminalId")
    private List<Order> orders;
}
