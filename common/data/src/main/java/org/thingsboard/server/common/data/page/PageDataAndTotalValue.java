/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.common.data.page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PageDataAndTotalValue<T> {

    /**
     * 总产能
     */
    private final Object totalValue;
    private final List<T> data;
    private final int totalPages;
    private final long totalElements;
    private final boolean hasNext;

    public PageDataAndTotalValue() {
        this(null,Collections.emptyList(), 0, 0, false);
    }

    @JsonCreator
    public PageDataAndTotalValue(@JsonProperty("totalValue") Object totalValue,
                                 @JsonProperty("data") List<T> data,
                                 @JsonProperty("totalPages") int totalPages,
                                 @JsonProperty("totalElements") long totalElements,
                                 @JsonProperty("hasNext") boolean hasNext) {
        this.totalValue =totalValue;
        this.data = data;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }

    public List<T> getData() {
        return data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public Object getTotalValue() {
        return totalValue;
    }

    public long getTotalElements() {
        return totalElements;
    }

    @JsonProperty("hasNext")
    public boolean hasNext() {
        return hasNext;
    }

    public <D> PageDataAndTotalValue<D> mapData(Function<T, D> mapper) {
        return new PageDataAndTotalValue<>(getTotalValue(),getData().stream().map(mapper).collect(Collectors.toList()), getTotalPages(), getTotalElements(), hasNext());
    }

}
