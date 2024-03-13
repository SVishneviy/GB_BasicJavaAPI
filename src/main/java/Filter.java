import lombok.Data;

@Data
public class Filter<T> {
    private final T content;
    private boolean selected;
}
