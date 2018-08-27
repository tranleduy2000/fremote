package com.duy.fremote.server;

public class MessageItem {
    public static final int TYPE_IN = 0;
    public static final int TYPE_OUT = 1;

    private int type;
    private String content;

    public MessageItem(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageItem{" +
                "type=" + type +
                ", content='" + content + '\'' +
                '}';
    }

}
