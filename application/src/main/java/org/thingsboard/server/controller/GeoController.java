package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.dao.hs.entity.vo.GeoVO;
import org.thingsboard.server.dao.hs.service.GeoService;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.io.IOException;
import java.util.List;

/**
 * 地理接口
 *
 * @author wwj
 * @since 2022.11.30
 */
@Api(value = "地理接口", tags = {"地理接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/geo")
public class GeoController extends BaseController {

    @Autowired
    private GeoService geoService;

    /**
     * 国外城市查询接口
     */
    @ApiOperation(value = "国外城市查询接口", notes = "根据用户角色语言环境返回结果，默认zh_cn")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cityName", value = "城市名称", paramType = "query", required = true),
            @ApiImplicitParam(name = "countryName", value = "国家名称", paramType = "query"),
            @ApiImplicitParam(name = "language", value = "语言环境", paramType = "query", defaultValue = "zh_cn"),
    })
    @GetMapping(value = "/cities")
    public List<GeoVO> uploadFile(@RequestParam String cityName,
                                  @RequestParam(required = false) String countryName,
                                  @RequestParam(required = false, defaultValue = "zh_cn") String language
    ) throws ThingsboardException, IOException, InterruptedException {
        checkParameter("cityName", cityName);
        return this.geoService.listCitiesByQuery(GeoVO.builder().cityName(cityName).countryName(countryName).language(language).build(), getCurrentUser().getId());
    }
}
