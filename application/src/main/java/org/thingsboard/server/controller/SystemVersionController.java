package org.thingsboard.server.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.thingsboard.server.common.data.exception.ThingsboardException;
import org.thingsboard.server.common.data.page.PageData;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.systemversion.SystemVersion;
import org.thingsboard.server.dao.systemversion.SystemVersionService;
import org.thingsboard.server.entity.systemversion.dto.AddSystemVersionDto;
import org.thingsboard.server.entity.systemversion.qry.SystemVersionQry;
import org.thingsboard.server.entity.systemversion.vo.SystemVersionVo;
import org.thingsboard.server.queue.util.TbCoreComponent;
import org.thingsboard.server.service.security.model.SecurityUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Api(value="系统版本",tags={"系统版本管理接口"})
@RequiredArgsConstructor
@RestController
@TbCoreComponent
@RequestMapping("/api/system")
public class SystemVersionController extends BaseController{

    @Autowired
    private SystemVersionService systemVersionService;

    /**
     * 新增/修改系统版本
     * @param addSystemVersionDto
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("新增/修改")
    @ApiImplicitParam(name = "addSystemVersionDto",value = "入参实体",dataType = "AddSystemVersionDto",paramType="body")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    @ResponseBody
    public SystemVersionVo saveSystemVersionVo(@RequestBody AddSystemVersionDto addSystemVersionDto) throws ThingsboardException {
        try {
            SystemVersionVo result = new SystemVersionVo();
            checkNotNull(addSystemVersionDto);
            checkParameterChinees("version", addSystemVersionDto.getVersion());
            SecurityUser currentUser = getCurrentUser();
            UUID tenantId = currentUser.getTenantId().getId();
            checkParameterChinees("tenantId", tenantId);
            SystemVersion systemVersion = addSystemVersionDto.toSystemVersion();
            systemVersion.setTenantId(tenantId);
            if(addSystemVersionDto.getId() == null){
                systemVersion.setCreatedUser(currentUser.getUuidId());
                systemVersion = systemVersionService.saveSystemVersion(systemVersion);
            }else {
                checkParameter("id", addSystemVersionDto.getId());
                systemVersion.setUpdatedUser(currentUser.getUuidId());
                systemVersion = systemVersionService.updSystemVersion(systemVersion);
            }
            if(systemVersion != null){
                result = new SystemVersionVo(systemVersion);
            }
            return result;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 删除
     * @param id
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("删除版本")
    @ApiImplicitParam(name = "id",value = "版本记录标识",dataType = "String",paramType="path",required = true)
    @RequestMapping(value = "/deSystemVersion/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delSystemVersion(@PathVariable("id") String id) throws ThingsboardException {
        try {
            //校验参数
            checkParameterChinees("id",id);
            systemVersionService.delSystemVersion(toUUID(id));
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     *  查询系统版本列表分页
     */
    @ApiOperation("查询系统版本列表分页")
    @ApiImplicitParam(name = "systemVersionQry",value = "条件内容",dataType = "SystemVersionQry",paramType = "query")
    @RequestMapping(value = "/findSystemVersionPage", params = {"pageSize", "page"}, method = RequestMethod.GET)
    @ResponseBody
    public PageData<SystemVersionVo> findSystemVersionPage(@RequestParam int pageSize,@RequestParam int page,
                                                           SystemVersionQry systemVersionQry) throws ThingsboardException {
        try {
            PageData<SystemVersionVo> voPageData = new PageData<>();
            List<SystemVersionVo> resultSystemVersionVos = new ArrayList<>();
            PageLink pageLink = createPageLink(pageSize, page,null,null,null);
            SystemVersion systemVersion = systemVersionQry.toSystemVersion();
            systemVersion.setTenantId(systemVersionQry.getTenantId() != null ? systemVersionQry.getTenantId() : getCurrentUser().getTenantId().getId());
            PageData<SystemVersion> systemVersionPageData = checkNotNull(systemVersionService.findSystemVersionPage(systemVersion, pageLink));
            List<SystemVersion> versionList = systemVersionPageData.getData();
            if(!CollectionUtils.isEmpty(versionList)){
                for (SystemVersion version : versionList) {
                    resultSystemVersionVos.add(new SystemVersionVo(version));
                }
            }
            voPageData = new PageData<>(resultSystemVersionVos,systemVersionPageData.getTotalPages(),systemVersionPageData.getTotalElements(),systemVersionPageData.hasNext());
            return voPageData;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 条件查询系统版本列表
     * @param systemVersionQry
     * @return
     * @throws ThingsboardException
     */
    @ApiOperation("条件查询系统版本列表")
    @ApiImplicitParam(name = "systemVersionQry",value = "条件内容",dataType = "SystemVersionQry",paramType = "query")
    @RequestMapping(value = "/getSystemVersionListByCdn", method = RequestMethod.GET)
    @ResponseBody
    public List<SystemVersionVo> getSystemVersionListByCdn(SystemVersionQry systemVersionQry)throws ThingsboardException{
        try {
            List<SystemVersionVo> result = new ArrayList<>();
            SystemVersion querySystemVersion = new SystemVersion();
            if(systemVersionQry != null){
                querySystemVersion = systemVersionQry.toSystemVersion();
                querySystemVersion.setTenantId(systemVersionQry.getTenantId() != null ? systemVersionQry.getTenantId() : getCurrentUser().getTenantId().getId());
            }
            List<SystemVersion> systemVersionList = systemVersionService.findSystemVersionList(querySystemVersion);
            if(!CollectionUtils.isEmpty(systemVersionList)){
                for (SystemVersion systemVersion:systemVersionList){
                    result.add(new SystemVersionVo(systemVersion));
                }
            }
            return result;
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    /**
     * 根据标识查询详情信息
     */
    @ApiOperation(value="根据标识查询详情信息")
    @ApiImplicitParam(name = "id",value = "当前版本id",dataType = "String",paramType="path",required = true)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SystemVersionVo getTenantById(@PathVariable("id") String id) throws ThingsboardException {
        try {
            checkParameterChinees("id",id);
            SystemVersion systemVersion = systemVersionService.findById(UUID.fromString(id));
            return new SystemVersionVo(systemVersion);
        } catch (Exception e) {
            throw handleException(e);
        }
    }

    @ApiOperation("获取系统当前版本")
    @RequestMapping(value = "/getSystemVersion", method = RequestMethod.GET)
    @ResponseBody
    public SystemVersionVo getSystemVersionMax() throws ThingsboardException {
        SystemVersion systemVersionMax = systemVersionService.getSystemVersionMax(getCurrentUser().getTenantId());
        if(systemVersionMax != null){
            return new SystemVersionVo(systemVersionMax);
        }
        return new SystemVersionVo("1.0.0", Uuids.unixTimestamp(Uuids.timeBased()));
    }
}
