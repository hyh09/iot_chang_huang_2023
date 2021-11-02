package org.thingsboard.server.service.userrole.Impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.dao.sql.role.service.TenantSysRoleService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.entity.ResultVo;
import org.thingsboard.server.entity.user.CodeKeyNum;
import org.thingsboard.server.entity.user.CodeVo;
import org.thingsboard.server.entity.user.UserVo;
import org.thingsboard.server.service.userrole.CheckSvc;
import org.thingsboard.server.service.userrole.SqlSplicingSvc;
import org.thingsboard.server.service.userrole.sqldata.SqlVo;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 校验
 */
@Slf4j
@Service
public class CheckImpl  implements CheckSvc {


    @Autowired private SqlSplicingSvc splicingSvc;
    @Autowired private TenantSysRoleService tenantSysRoleService;
    @Autowired private UserService userService;

    @Override
    public Boolean checkValueByKey(UserVo vo) {
        log.info("调用查询菜单列表的入参{}",vo);
        SqlVo sqlVo= splicingSvc.getCountUserSqlByVo(vo);
        log.debug("调用查询菜单列表的入参{},通过sql{},查询到得结果{}",sqlVo.getParam(),sqlVo.getSql());
        Long count= tenantSysRoleService.queryContListSqlLocal(sqlVo.getSql(),sqlVo.getParam());
        log.info("调用查询菜单列表的入参{},通过sql{},查询到得结果{}",sqlVo.getParam(),sqlVo.getSql(),count);
        return  (count>0?true:false);
    }

    /**
     * 查询用户/角色编码
     * @param vo
     * @return
     */
    @Override
    public Object queryCode(CodeVo vo) {
        try {
            log.info("查询用户/角色编码的入参:{}", vo);
            SqlVo sqlVo = splicingSvc.getUserCode(vo);
            List<Map> codeVos = tenantSysRoleService.queryAllListSqlLocal(sqlVo.getSql(), sqlVo.getParam(), Map.class);
            if (CollectionUtils.isEmpty(codeVos)) {
                log.info("当前的库中没有数据,返回初始化的数据");
                CodeKeyNum codeKeyNum = CodeKeyNum.getValueByKey(vo.getKey());
                return ResultVo.getSuccessFul( format("",codeKeyNum));

            }
            CodeKeyNum codeKeyNum = CodeKeyNum.getValueByKey(vo.getKey());
            if(codeKeyNum == null)
            {
                return ResultVo.builderFail("0","入参key不在配置内");
            }
            log.info("打印当前数据:{},codeKeyNum{}",codeVos,codeKeyNum.getKey());
            log.info("打印当前数据codeVo:{}",codeVos.get(0));
            String code = (String) codeVos.get(0).get("code");
            log.info("打印当前数据,code{}",code);
            if(StringUtils.isEmpty(code))
            {
                return ResultVo.getSuccessFul( format("",codeKeyNum));
            }

            return ResultVo.getSuccessFul( format(code,codeKeyNum));
        }catch (Exception e)
        {
             e.printStackTrace();
             log.info("打印查询用户/角色编码的异常:{}",e);
            return ResultVo.builderFail("0","查询用户/角色编码的异常"+e.getMessage());
        }
    }

    /**
     * 查询用户 /角色编码
     * @param vo
     * @return
     */
    @Override
    public Object queryCodeNew(CodeVo vo,TenantId tenantId) {
          if(vo.getKey().equals(CodeKeyNum.key_user.getKey()))
          {
            return   getUserAvailableCode(tenantId);
          }

        return geRoleAvailableCode(tenantId);
    }




    public String getUserAvailableCode(TenantId tenantId) {
        List<String> codes = userService.findAllCodesByTenantId(tenantId.getId());
       if(CollectionUtils.isEmpty(codes))
       {
           return CodeKeyNum.key_user.getValue()+CodeKeyNum.key_user.getInit();
       }

      return  null;
    }



    public String geRoleAvailableCode(TenantId tenantId) {
        var codes = tenantSysRoleService.findAllCodesByTenantId();
        if (codes.isEmpty()) {
            return "05";
        } else {
            var ints = codes.stream().map(e -> Integer.valueOf(e.split("")[1])).sorted().collect(Collectors.toList());
            int start = 0;
            while (true) {
                if (ints.size() - 1 == start) {
                    return "" + String.format("%04d", start + 2);
                }
                if (!ints.get(start).equals(start + 1)) {
                    return "" + String.format("%04d", start + 1);
                }
                start += 1;
            }
        }
    }




    private String format(String str,  CodeKeyNum codeKeyNum)
    {

        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        String m1=  m.replaceAll("").trim();
        if(StringUtils.isEmpty(m1))
        {
            m1=codeKeyNum.getInit();
        }
        Integer integer  = Integer.valueOf(m1) +1;
       String result = String.format(codeKeyNum.getCheckSing(), integer);
        log.info("result=====================:{}",result);
        return  codeKeyNum.getValue()+result;
    }


}
