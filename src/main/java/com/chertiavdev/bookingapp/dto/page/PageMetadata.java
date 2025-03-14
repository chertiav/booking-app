package com.chertiavdev.bookingapp.dto.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Metadata about the current page and total data count")
public class PageMetadata {
    @Schema(description = "Current page number", example = "0")
    private int currentPage;
    @Schema(description = "Number of items per page", example = "20")
    private int pageSize;
    @Schema(description = "Total number of pages", example = "1")
    private int totalPageCount;
    @Schema(description = "Total number of elements", example = "1")
    private long totalElementCount;
}
