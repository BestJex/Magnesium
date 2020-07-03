package net.onebean.api.mngt.service.impl;import com.alibaba.fastjson.JSON;import com.alibaba.fastjson.JSONObject;import com.alibaba.fastjson.serializer.SerializerFeature;import net.onebean.api.mngt.vo.ServerHostNodeVo;import net.onebean.api.mngt.vo.UpSteamSyncNodeVo;import net.onebean.api.mngt.common.ConfPathHelper;import net.onebean.api.mngt.service.ManualUpdateServerNodeService;import net.onebean.api.mngt.service.ServerInfoService;import net.onebean.api.mngt.service.UpSteamNodeService;import net.onebean.api.mngt.service.UpgradeNginxConfService;import net.onebean.api.mngt.vo.ConfResult;import net.onebean.util.*;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;import java.nio.file.Paths;import java.util.ArrayList;import java.util.Collections;import java.util.List;import java.util.Optional;@Servicepublic class ManualUpdateServerNodeServiceImpl implements ManualUpdateServerNodeService {    private final static Logger logger = LoggerFactory.getLogger(ManualUpdateServerNodeServiceImpl.class);    private final static Boolean IS_SYNC_UPDATE_NGINX = false;    private final static String UPSTREAM_FTL_PATH = "/upstream/upstream.ftl";    private final static String HOST_FTL_PATH = "/host/host.ftl";    private final static String LOG_FTL_PATH = "/source/log.ftl";    @Autowired    private UpgradeNginxConfService upgradeNginxConfService;    @Autowired    private UpSteamNodeService upSteamNodeService;    @Autowired    private ServerInfoService serverInfoService;    @Override    public Boolean updateAllNginxUpSteamConf() {        /*查询可用的upsteam节点 生成upsteam.conf文件*/        ConfResult confResult = initConfResult();        /*异步更新nginx节点配置*/        upgradeNginxConfService.updateAllRemoteNginxConf(confResult.getCoverFiles(), confResult.getRemoveFiles(), IS_SYNC_UPDATE_NGINX);        return true;    }    private ConfResult initConfResult() {        ConfResult confResult = new ConfResult();        //获取需要同步的 UpSteamNode 节点        List<UpSteamSyncNodeVo> upSteamNodeVos = upSteamNodeService.findSyncNode();        String upSteamNodeVosPath = handleUpSteamNode(upSteamNodeVos);        //添加 UpSteam 文件的路径        confResult.addCoverFile(upSteamNodeVosPath);        confResult.addRemoveFile(upSteamNodeVosPath);        //添加 host 配置文件路径        List<String> hostNode = handleHostNode(upSteamNodeVos);        if (CollectionUtil.isNotEmpty(hostNode)) {            confResult.addCoverFiles(hostNode);            confResult.addRemoveFiles(hostNode);        }        /*将本地文件打包成压缩文件*/        compressConfig();        return confResult;    }    /**     * 将本地文件进行打包处理     */    private void compressConfig() {        /*删除本地压缩文件的目录*/        IOUtils.deleteFile(ConfPathHelper.getLocalTarFilePath());        List<String> paths = new ArrayList<>();        paths.add(ConfPathHelper.getLocalConfDir());        paths.add(ConfPathHelper.getLocalHostConfDir());        paths.add(ConfPathHelper.getLocalLogPath());        /*打包文件 指定文件和目标路径*/        TarFileUtil.compress(paths, ConfPathHelper.getLocalTarFilePath());    }    @SuppressWarnings("unchecked")    private List<String> handleHostNode(List<UpSteamSyncNodeVo> upSteamNodeVos) {        StringBuilder stringBuilder = new StringBuilder();        List<ServerHostNodeVo> serverHostNodeVos = serverInfoService.findSyncHostNode(upSteamNodeVos);        if (CollectionUtil.isEmpty(serverHostNodeVos)){            serverHostNodeVos = Collections.EMPTY_LIST;        }        logger.info("生成host.conf文件：" + JSON.toJSONString(serverHostNodeVos, SerializerFeature.WriteMapNullValue));        /*在本地创建目录*/        IOUtils.mkDir(ConfPathHelper.getLocalHostConfDir());        IOUtils.mkDir(ConfPathHelper.getLocalLogPath());        /*生成对应内容 并写成文件*/        for (ServerHostNodeVo s : serverHostNodeVos) {            String upSteamNodeName = Optional.of(s).map(ServerHostNodeVo::getUpsteamNodeName).orElse("");            if (StringUtils.isNotEmpty(upSteamNodeName)){                JSONObject param = new JSONObject();                param.put("hostNode", s);                String localHostConfPath = Paths.get(ConfPathHelper.getLocalBasePath(),"front", upSteamNodeName+".conf").toFile().getAbsolutePath();                FreeMarkerTemplateUtils.generateFile(param,localHostConfPath,HOST_FTL_PATH);                String localAccessLogPath = Paths.get(ConfPathHelper.getLocalLogPath(), upSteamNodeName+"-access.log").toFile().getAbsolutePath();                String localErrorLogPath = Paths.get(ConfPathHelper.getLocalLogPath(), upSteamNodeName+"-error.log").toFile().getAbsolutePath();                FreeMarkerTemplateUtils.generateFile(param,localHostConfPath,HOST_FTL_PATH);                FreeMarkerTemplateUtils.generateFile(param,localAccessLogPath,LOG_FTL_PATH);                FreeMarkerTemplateUtils.generateFile(param,localErrorLogPath,LOG_FTL_PATH);                stringBuilder.append(ConfPathHelper.getEcsRelativePath(localHostConfPath)).append(",");                stringBuilder.append(ConfPathHelper.getEcsRelativePath(localAccessLogPath)).append(",");                stringBuilder.append(ConfPathHelper.getEcsRelativePath(localErrorLogPath)).append(",");            }        }        if (stringBuilder.length() > 0){            String resStr =  stringBuilder.substring(0,stringBuilder.length() -1);            return CollectionUtil.stringArr2List(resStr.split(","));        }        return Collections.EMPTY_LIST;    }    @SuppressWarnings("unchecked")    private String handleUpSteamNode(List<UpSteamSyncNodeVo> upSteamNodeVos) {        if (CollectionUtil.isEmpty(upSteamNodeVos)){            upSteamNodeVos = Collections.EMPTY_LIST;        }        logger.info("生成upstream.conf文件：" + JSON.toJSONString(upSteamNodeVos, SerializerFeature.WriteMapNullValue));        /*在本地创建目录*/        IOUtils.mkDir(ConfPathHelper.getLocalConfDir());        /*生成对应内容 并写成文件*/        JSONObject param = new JSONObject();        param.put("upSteamNodes", upSteamNodeVos);        String localUpstreamPath = Paths.get(ConfPathHelper.getLocalBasePath(),"conf.d", "upstream.conf").toFile().getAbsolutePath();        FreeMarkerTemplateUtils.generateFile(param,localUpstreamPath,UPSTREAM_FTL_PATH);        return ConfPathHelper.getEcsRelativePath(localUpstreamPath);    }}