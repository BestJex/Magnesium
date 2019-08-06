package net.onebean.user.mngt.common;

public enum MqQueueNameEnum {


    UAG_CLOUD_CONTROL_OPERATION_LOG("uag.cloud.control.operation.log"),
    UAG_USER_ACCOUNT_RESET_PASSWORD("uag.user.account.reset.password"),
    ;

    private String name;

    MqQueueNameEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}