/**
 * Copyright 2016 ZTE Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openo.commontosca.catalog.model.plan.wso2;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RestfulClient {
  private static final String HTTP = "http";
  private static final Logger logger = LoggerFactory.getLogger(RestfulClient.class);

  enum HttpMethod {
    GET, POST, PUT, DELETE
  }

  /**
   * execute http.
   * @param method http method
   * @param ip ip
   * @param port port
   * @param url url
   * @param body http body
   * @return RestResponse
   */
  public static RestResponse executeHttp(HttpMethod method, String ip, int port, String url,
      HttpEntity body) {
    CloseableHttpClient httpclient = HttpClients.createDefault();
    HttpResponse httpResponse = null;
    RestResponse result = new RestResponse();
    try {
      // specify the host, protocol, and port
      HttpHost target = new HttpHost(ip, port, HTTP);
      // specify the get request
      HttpRequest request = getRequest(method, url, body);
      httpResponse = httpclient.execute(target, request);
      HttpEntity entity = httpResponse.getEntity();
      if (entity != null) {
        result.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        result.setResult(EntityUtils.toString(entity));
      }
    } catch (Exception e1) {
      logger.error("send get rest request error:", e1.getMessage());
    } finally {
      if (httpclient != null) {
        try {
          httpclient.close();
        } catch (IOException e2) {
          logger.error("close httpclient error:", e2.getMessage());
        }
      }
    }
    return result;
  }

  private static HttpRequest getRequest(HttpMethod method, String url, HttpEntity body) {
    HttpRequest request = null;
    switch (method) {
      case GET:
        request = new HttpGet(url);
        break;
      case POST:
        request = new HttpPost(url);
        ((HttpPost) request).setEntity(body);
        break;
      case PUT:
        request = new HttpPut(url);
        ((HttpPut) request).setEntity(body);
        break;
      case DELETE:
        request = new HttpDelete(url);
        break;
      default:
        break;
    }
    return request;
  }

  public static String get(String ip, int port, String url) {
    return executeHttp(HttpMethod.GET, ip, port, url, null).getResult();
  }

  public static RestResponse delete(String ip, int port, String url) {
    return executeHttp(HttpMethod.GET, ip, port, url, null);
  }

  public static RestResponse post(String ip, int port, String url, HttpEntity requestBody) {
    return executeHttp(HttpMethod.POST, ip, port, url, requestBody);
  }

}
