package com.chertiavdev.bookingapp.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@Schema(description = "Paginated response containing content and metadata")
public class PageResponse<T> {
    @Schema(description = "List of items on the current page")
    private List<T> content;
    @Schema(description = "Metadata about pagination")
    private PageMetadata metadata;

    public static <T> PageResponse<T> of(Page<T> page) {
        return new PageResponse<>(page.getContent(), getMetaData(page));
    }

    private static <T> PageMetadata getMetaData(Page<T> page) {
        return new PageMetadata(
                page.getNumber(),
                page.getSize(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
