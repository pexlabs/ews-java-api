package microsoft.exchange.webservices.data.core;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public final class HttpUtil {
  private HttpUtil() {}

  public static HttpClientBuilder newHttpClientBuilder(HttpClientConnectionManager connectionManager) {
    return HttpClients.custom()
        .setConnectionManager(connectionManager)
        .setTargetAuthenticationStrategy(new CookieProcessingTargetAuthenticationStrategy());
  }

  public static HttpClientConnectionManager newPoolingConnectionManager(int maxPoolSize) {
    if (maxPoolSize <= 0) {
      throw new IllegalArgumentException("Invalid max pool size: " + maxPoolSize);
    }
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(maxPoolSize);
    cm.setDefaultMaxPerRoute(maxPoolSize);
    return cm;
  }

}
