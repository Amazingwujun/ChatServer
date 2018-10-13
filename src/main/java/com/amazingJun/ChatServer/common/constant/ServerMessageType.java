package com.amazingJun.ChatServer.common.constant;

public enum ServerMessageType {
    //@formatter:off

    /** 异常响应 */
    ERROR_RESP((byte)100),

    /** 心跳请求 */
    HEARTBEAT_REQ((byte)0),

    /** 心跳响应 */
    HEARTBEAT_RESP((byte)1),

    /** 登录请求 */
    LOGIN_REQ((byte)2),

    LOGIN_RESP((byte)3);

    //@formatter:on

    private byte code;

    ServerMessageType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
