package net.onebean.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.onebean.gateway.vo.AppBindingApiReq;
import net.onebean.gateway.common.ErrorCodesEnum;
import net.onebean.gateway.dao.AppApiDao;
import net.onebean.gateway.model.AppApi;
import net.onebean.gateway.service.AppApiService;
import net.onebean.core.base.BaseBiz;
import net.onebean.core.error.BusinessException;
import net.onebean.core.form.Parse;
import net.onebean.util.CollectionUtil;
import net.onebean.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
* @author Icc
* @description app info join api info serviceImpl
* @date 2019-01-25 20:13:35
*/
@Service
public class AppApiServiceImpl extends BaseBiz<AppApi, AppApiDao> implements AppApiService {

    private final static Logger logger = LoggerFactory.getLogger(AppApiServiceImpl.class);



    @Override
    public Boolean bindApi(AppBindingApiReq req) {
        String appId = Optional.ofNullable(req).map(AppBindingApiReq::getAppId).orElse(null);
        Integer operatorId = Optional.ofNullable(req).map(AppBindingApiReq::getOperatorId).orElse(null);
        String operatorName = Optional.ofNullable(req).map(AppBindingApiReq::getOperatorName).orElse(null);
        List<String> apiIds = Optional.ofNullable(req).map(AppBindingApiReq::getApiIds).orElse(null);
        List<AppApi> AppApiArr = new ArrayList<>();
        if (StringUtils.isEmpty(appId)){
            throw new BusinessException(ErrorCodesEnum.REQUEST_PARAM_ERROR.code(),ErrorCodesEnum.REQUEST_PARAM_ERROR.msg()+"filed of appId");
        }

        if (CollectionUtil.isEmpty(apiIds)){
            throw new BusinessException(ErrorCodesEnum.REQUEST_PARAM_ERROR.code(),ErrorCodesEnum.REQUEST_PARAM_ERROR.msg()+"filed of apiIds");
        }
        logger.debug("AppApiServiceImpl bindApi method appId = "+ JSON.toJSONString(appId, SerializerFeature.WriteMapNullValue));
        logger.debug("AppApiServiceImpl bindApi method apiIds = "+ JSON.toJSONString(apiIds, SerializerFeature.WriteMapNullValue));
        for (String apiId : apiIds) {
            AppApi appApi = new AppApi();
            appApi.setApiId(Parse.toInt(apiId));
            appApi.setAppId(Parse.toInt(appId));
            appApi.setOperatorName(operatorName);
            appApi.setOperatorId(operatorId);
            AppApiArr.add(appApi);
        }
        try {
            this.saveBatch(AppApiArr);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodesEnum.INSERT_DATA_ERROR.code(),ErrorCodesEnum.INSERT_DATA_ERROR.msg()+"appApiService.saveBatch");
        }
        return true;
    }

    @Override
    public Boolean unBindApi(AppBindingApiReq req) {
        String appId = Optional.ofNullable(req).map(AppBindingApiReq::getAppId).orElse(null);
        List<String> apiIds = Optional.ofNullable(req).map(AppBindingApiReq::getApiIds).orElse(null);
        if (StringUtils.isEmpty(appId)){
            throw new BusinessException(ErrorCodesEnum.REQUEST_PARAM_ERROR.code(),ErrorCodesEnum.REQUEST_PARAM_ERROR.msg()+"filed of appId");
        }
        if (CollectionUtil.isEmpty(apiIds)){
            throw new BusinessException(ErrorCodesEnum.REQUEST_PARAM_ERROR.code(),ErrorCodesEnum.REQUEST_PARAM_ERROR.msg()+"filed of apiIds");
        }
        try {
            baseDao.deleteByAppidAndApiIds(appId,apiIds);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodesEnum.INSERT_DATA_ERROR.code(),ErrorCodesEnum.INSERT_DATA_ERROR.msg()+"appApiService.saveBatch");
        }
        return true;
    }

    @Override
    public List<AppApi> findPrivateTokenAuthApiBind() {
        return baseDao.findPrivateTokenAuthApiBind();
    }
}