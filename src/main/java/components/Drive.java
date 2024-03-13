package components;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = false, chain = true)
@Data
public class Drive {
    private final Type type;
    private final int volume;

    public enum Type {
        HDD,
        SSD,
        SSD_M2
    }
}
