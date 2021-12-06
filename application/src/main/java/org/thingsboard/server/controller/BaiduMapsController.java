package org.thingsboard.server.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.common.util.baidumap.BaiduMaps;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.queue.util.TbCoreComponent;

import java.util.Map;

@Api(value = "百度地图API",tags={"百度地图接口"})
@RestController
@TbCoreComponent
@RequestMapping("/api/baiduApi")
public class BaiduMapsController {

    /**
     * 行政区划区域检索
     * @param keyword
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("行政区划区域检索")
    @ApiImplicitParam(name = "keyword",value = "地址",dataType = "string",paramType = "query",required = true)
    @RequestMapping(value = "/getBoroughList", method = RequestMethod.GET)
    @ResponseBody
    public Map getBoroughList(@RequestParam String keyword,@RequestParam String query) throws ThingsboardException {
        Map<String, String> map = BaiduMaps.getBoroughList(query,keyword);
        System.out.println(map);
        return map;
    }

    @ApiOperation("查询地址经纬度")
    @ApiImplicitParam(name = "address",value = "地址",dataType = "string",paramType = "query",required = true)
    @RequestMapping(value = "/getCoordinate", method = RequestMethod.GET)
    @ResponseBody
    public Map getCoordinate(@RequestParam String address) throws ThingsboardException {
        Map<String,String> map = BaiduMaps.getCoordinate(address);
        System.out.println(map);
        return map;
    }

}
