package components;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = false, chain = true)
@Data
public final class RAM {
    private final Type type;
    private final int volume;

    public enum Type {
        DDR3,
        DDR4,
        DDR5
    }
}
