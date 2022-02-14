package org.thingsboard.server.dao.hs.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 最新的ts
 *
 * @author wwj
 * @since 2022.2.14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestKvDTO {
    private UUID id;
    private Long ts;
}
