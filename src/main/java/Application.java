import components.Drive;
import components.RAM;
import enums.Color;
import enums.OperationSystem;
import enums.Processor;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Application {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Map<Integer, List<?>> filters = new HashMap<>();
    private static final Map<Integer, Predicate<Notebook>> filterPredicates = new HashMap<>();
    private static final int APPLY_FILTER = 9;
    private static final int EXIT_CODE = 0;

    public static void main(String[] args) {
        Set<Notebook> notebooks = new HashSet<>();
        generateNotebooks(notebooks);

        Map<String, String> filter = new HashMap<>();
        addFilters(filter);

        while (true) {
            displayFilterOptions(filter);

            int userChoice = getUserInput();
            if (userChoice == APPLY_FILTER) {
                break;
            }

            switch (userChoice) {
                case 1 -> addFilterAndPredicate(
                        1,
                        notebooks
                                .stream()
                                .collect(Collectors.groupingBy(Notebook::getVendor))
                                .keySet()
                                .stream()
                                .toList(),
                        filters.get(1),
                        Notebook::getVendor
                );
                case 2 -> addFilterAndPredicate(
                        2,
                        Arrays.stream(Color.values()).toList(),
                        filters.get(2),
                        Notebook::getColor
                );
                case 3 -> addFilterAndPredicate(
                        3,
                        Arrays.stream(RAM.Type.values()).toList(),
                        filters.get(3),
                        n -> n.getRam().getType()
                );
                case 4 -> addVolumeFilter(4, n -> n.getRam().getVolume());
                case 5 -> addFilterAndPredicate(
                        5,
                        Arrays.stream(Drive.Type.values()).toList(),
                        filters.get(5),
                        n -> n.getDrive().getType()
                );
                case 6 -> addVolumeFilter(6, n -> n.getDrive().getVolume());
                case 7 -> addFilterAndPredicate(
                        7,
                        Arrays.stream(OperationSystem.values()).toList(),
                        filters.get(7),
                        Notebook::getOperationSystem
                );
                case 8 -> addFilterAndPredicate(
                        8,
                        Arrays.stream(Processor.values()).toList(),
                        filters.get(8),
                        Notebook::getProcessor
                );
                case 0 -> System.exit(EXIT_CODE);
                default -> System.out.println("Incorrect value. Please try again.");
            }
        }

        notebooks.stream()
                .filter(n -> filterPredicates.values().stream().allMatch(p -> p.test(n)))
                .forEach(System.out::println);
    }

    private static void generateNotebooks(Set<Notebook> notebooks) {
        Random random = new Random();
        int[] ramVolumes = {1024, 2048, 4096, 6144, 8192, 16384};
        int[] driveVolumes = {500, 1000, 1500, 2000, 4000, 5000};

        for (int i = 0; i < 50; i++) {
            String vendor;
            Color color;
            RAM.Type ramType;
            Drive.Type driveType;
            int ramVolume;
            int driveVolume;
            Processor processor;
            OperationSystem operationSystem;

            switch (random.nextInt(5)) {
                case 0:
                    vendor = "Acer";
                    color = Color.BLACK;
                    processor = Processor.INTEL;
                    operationSystem = OperationSystem.WINDOWS;
                    break;
                case 1:
                    vendor = "Asus";
                    color = Color.GREEN;
                    processor = Processor.AMD;
                    operationSystem = OperationSystem.LINUX;
                    break;
                case 2:
                    vendor = "Apple";
                    color = Color.WHITE;
                    processor = Processor.INTEL;
                    operationSystem = OperationSystem.MACOS;
                    break;
                case 3:
                    vendor = "Gigabyte";
                    color = Color.GREY;
                    processor = Processor.AMD;
                    operationSystem = OperationSystem.WINDOWS;
                    break;
                case 4:
                    vendor = "Lenovo";
                    color = Color.BLUE;
                    processor = Processor.INTEL;
                    operationSystem = OperationSystem.LINUX;
                    break;
                default:
                    vendor = "Unknown";
                    color = Color.RED;
                    processor = Processor.AMD;
                    operationSystem = OperationSystem.LINUX;
                    break;
            }

            ramType = RAM.Type.values()[random.nextInt(RAM.Type.values().length)];
            ramVolume = ramVolumes[random.nextInt(ramVolumes.length)];

            driveType = Drive.Type.values()[random.nextInt(Drive.Type.values().length)];
            driveVolume = driveVolumes[random.nextInt(driveVolumes.length)];

            Notebook notebook = new Notebook(vendor, color)
                    .setDrive(new Drive(driveType, driveVolume))
                    .setProcessor(processor)
                    .setRam(new RAM(ramType, ramVolume))
                    .setOperationSystem(operationSystem);

            notebooks.add(notebook);
        }
    }

    private static void displayFilterOptions(Map<String, String> filter) {
        System.out.println("Filtering by:");

        filter.forEach((k, v) -> System.out.println(k + " - " + v));

        System.out.print("Enter number (0 to exit): ");
    }

    private static int getUserInput() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");

            scanner.next();
            return getUserInput();
        }
    }

    private static void addFilters(Map<String, String> filter) {
        filter.put("1", "Vendor");
        filter.put("2", "Color");
        filter.put("3", "RAM");
        filter.put("4", "RAM volume");
        filter.put("5", "Drive");
        filter.put("6", "Drive volume");
        filter.put("7", "Operation System");
        filter.put("8", "Processor");
        filter.put("9", "Apply a filter");
    }

    private static <T> void addFilterAndPredicate(int filterNumber, List<T> values, List<?> filter,
                                                  Function<Notebook, T> mapper) {
        List<?> result = getFilter(values, filter);

        if (result.isEmpty()) {
            filters.remove(filterNumber);
            filterPredicates.remove(filterNumber);
            return;
        }

        filters.put(filterNumber, result);
        filterPredicates.put(filterNumber, n -> filters.get(filterNumber).contains(mapper.apply(n)));
    }

    private static void addVolumeFilter(int filterNumber, Function<Notebook, Integer> volumeExtractor) {
        System.out.print("Enter volume: ");

        int value = scanner.nextInt();

        if (value <= 0) {
            filterPredicates.remove(filterNumber);
            return;
        }

        filterPredicates.put(filterNumber, n -> volumeExtractor.apply(n) >= value);
    }

    private static <T> List<T> getFilter(List<T> options, List<?> selectedOptions) {
        Map<Integer, Filter<T>> filterMap = new HashMap<>();
        List<T> selected = new ArrayList<>();

        int index = 1;
        for (T option : options) {
            Filter<T> filter = new Filter<>(option);

            if (selectedOptions != null && selectedOptions.contains(option)) {
                filter.setSelected(true);
                selected.add(option);
            }

            filterMap.put(index++, filter);
        }

        while (true) {
            filterMap.forEach((k, val) ->
                    System.out.println(k + " - " + val.getContent() + " - " + val.isSelected())
            );
            System.out.print("Enter number (0 to finish): ");

            int num = getUserInput();
            if (num == 0) {
                break;
            }

            Filter<T> filter = filterMap.get(num);
            if (filter.isSelected()) {
                filter.setSelected(false);
                selected.remove(filter.getContent());
                continue;
            }

            filter.setSelected(true);
            selected.add(filter.getContent());
        }

        return selected;
    }
}
