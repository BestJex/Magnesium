package net.onebean.gateway.enumModel;

public enum ServerMachineTypeEnum {

    //服务节点类型,0：openresty，1：kubernetes-master
    OPENRESTY("0", "openresty"),
    KUBERNETES_MASTER("1", "kubernetes-master"),
            ;

    ServerMachineTypeEnum() {
    }

    private ServerMachineTypeEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }


    private String key;
    private String value;

    public static String getKeyByValue(String value) {
        for (ServerMachineTypeEnum s : ServerMachineTypeEnum.values()) {
            if (s.getValue().equals(value)) {
                return s.getKey();
            }
        }
        return "";
    }

    public static String getValueByKey(String key) {
        for (ServerMachineTypeEnum s : ServerMachineTypeEnum.values()) {
            if (s.getKey().equals(key)) {
                return s.getValue();
            }
        }
        return "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}