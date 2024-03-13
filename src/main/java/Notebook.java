import components.Drive;
import components.RAM;
import enums.Color;
import enums.OperationSystem;
import enums.Processor;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = false, chain = true)
@Data
public class Notebook {
    private final String vendor;
    private final Color color;
    private RAM ram;
    private Drive drive;
    private OperationSystem operationSystem;
    private Processor processor;
}
