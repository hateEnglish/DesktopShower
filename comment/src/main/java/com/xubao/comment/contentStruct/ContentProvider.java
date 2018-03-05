package com.xubao.comment.contentStruct;

/**
 * @Author xubao
 * @Date 2018/3/3
 */
public interface ContentProvider {
    Content getContent() throws Exception;
    int getNextContentNumber();
}
