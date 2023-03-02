package org.thingsboard.server.dao.hs.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.id.UserId;
import org.thingsboard.server.dao.entity.AbstractEntityService;
import org.thingsboard.server.dao.hs.entity.vo.GeoVO;
import org.thingsboard.server.dao.hs.service.ClientService;
import org.thingsboard.server.dao.hs.service.CommonService;
import org.thingsboard.server.dao.hs.service.GeoService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * geo Service Impl
 *
 * @author wwj
 * @since 2021.11.26
 */
@Service
@Slf4j
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class GeoServiceImpl extends AbstractEntityService implements GeoService, CommonService {

    @Value("${hs.geo.address}")
    private String geoAddress;

    private final RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    private ClientService clientService;


    /**
     * 创建人: wwj
     * 创建日期: 2022-01-05
     * 创建的时候描述: 查询城市列表 TODO 结果必须缓存在您这边。重复发送相同查询的客户端可能被归类为错误和阻塞。
     * ####################################
     * 补充注释人： wb04
     * 补充日期: 2023-03-02
     * 补充描述： 1 . https://nominatim.openstreetmap.org/search?city={cityName}&country={countryName}&accept-language={acceptLanguage}&format=json&addressdetails=1&limit=10
     *
     * @param geoVO  参数
     * @param userId 用户Id
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    public List<GeoVO> listCitiesByQuery(GeoVO geoVO, UserId userId) throws IOException, InterruptedException {
        /** 2023-03-02 这个是干嘛用的？ 查询当前用户的 附加信息*/
        Optional.ofNullable(this.clientService.getUserByUserId(userId)).map(User::getAdditionalInfo).map(v -> v.get("lang")).map(JsonNode::asText).ifPresent(geoVO::setLanguage);
        return CompletableFuture.supplyAsync(() -> restTemplateBuilder.build().getForEntity(geoAddress + "/search?city={cityName}&country={countryName}&accept-language={acceptLanguage}&format=json&addressdetails=1&limit=10", ArrayNode.class, geoVO.getCityName(), geoVO.getCountryName(), geoVO.getLanguage()))
                .thenApplyAsync(responseEntity -> Optional.ofNullable(responseEntity.getBody())
                        .map(v -> IntStream.range(0, v.size()).mapToObj(v::get))
                        .map(v -> v.filter(f -> Optional.ofNullable(f.get("osm_type")).map(JsonNode::asText).filter(l -> !"way".equals(l)).isPresent())
                                .map(e -> {
                                    var address = Optional.ofNullable(e.get("address"));
                                    return GeoVO.builder()
                                            .language(geoVO.getLanguage().trim())
                                            .latitude(Optional.ofNullable(e.get("lat")).map(JsonNode::asText).orElse(null))
                                            .longitude(Optional.ofNullable(e.get("lon")).map(JsonNode::asText).orElse(null))
                                            .cityName(address.map(g -> {
                                                if (g.get("city") == null)
                                                    return g.get("place");
                                                return g.get("city");
                                            }).map(JsonNode::asText).orElse(null))
                                            .countryName(address.map(g -> {
                                                if (g.get("country") == null)
                                                    return g.get("place");
                                                return g.get("country");
                                            }).map(JsonNode::asText).map(this::toTrueCountry).orElse(null))
                                            .postcode(address.map(k -> k.get("postcode")).map(JsonNode::asText).orElse(null))
                                            .displayName(Optional.ofNullable(e.get("display_name")).map(JsonNode::asText).map(this::toTrueDisplayName).orElse(null))
                                            .build();
                                })).map(v -> v.collect(Collectors.toList())).orElse(Lists.newArrayList())).join();
    }

    @Autowired
    public void setClientService(ClientService clientService) {
        this.clientService = clientService;
    }
}
