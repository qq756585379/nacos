
package com.alibaba.nacos.common.http;

import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.http.param.Query;
import com.alibaba.nacos.common.model.RestResult;

import java.lang.reflect.Type;

@Deprecated
@SuppressWarnings("all")
public interface NSyncHttpClient extends NHttpClient {

    /**
     * http get
     *
     * @param url    url
     * @param header http header param
     * @param query  http query param
     * @param token  return type
     * @return {@link RestResult <T>}
     * @throws Exception
     */
    <T> RestResult<T> get(String url, Header header, Query query, Type token) throws Exception;

    /**
     * get request, may be pulling a lot of data
     *
     * @param url    url
     * @param header http header param
     * @param query  http query param
     * @param body   get with body
     * @param token  return type
     * @return {@link RestResult <T>}
     * @throws Exception
     */
    <T> RestResult<T> getLarge(String url, Header header, Query query, Object body, Type token) throws Exception;

    /**
     * http delete
     *
     * @param url    url
     * @param header http header param
     * @param query  http query param
     * @param token  return type
     * @return {@link RestResult <T>}
     * @throws Exception
     */
    <T> RestResult<T> delete(String url, Header header, Query query, Type token) throws Exception;

    /**
     * http put
     *
     * @param url    url
     * @param header http header param
     * @param query  http query param
     * @param body   http body param
     * @param token  return type
     * @return {@link RestResult}
     * @throws Exception
     */
    <T> RestResult<T> put(String url, Header header, Query query, Object body, Type token) throws Exception;

    /**
     * http post
     *
     * @param url    url
     * @param header http header param
     * @param query  http query param
     * @param body   http body param
     * @param token  return type
     * @return {@link RestResult}
     * @throws Exception
     */
    <T> RestResult<T> post(String url, Header header, Query query, Object body, Type token) throws Exception;

}
