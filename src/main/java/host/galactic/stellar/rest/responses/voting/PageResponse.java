package host.galactic.stellar.rest.responses.voting;

import java.util.List;

public record PageResponse<T>(List<T> items, int totalPages) {
}
