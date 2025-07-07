package host.galactic.data.utils;

import java.util.List;

public record Page<T> (List<T> items, int totalPages) {
}
