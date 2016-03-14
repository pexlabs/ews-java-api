package microsoft.exchange.webservices.data.core;

import microsoft.exchange.webservices.data.autodiscover.AutodiscoverService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class ExchangeFactory {
  protected final CloseableHttpClient httpClient;
  protected ExchangeVersion version = DEFAULT_VERSION;
  protected Integer timeout;

  public static final ExchangeVersion DEFAULT_VERSION = ExchangeVersion.Exchange2010_SP2;

  public static ExchangeFactory defaultFactory() {
    return DefaultFactory.INSTANCE;
  }

  private static class DefaultFactory {
    private static final ExchangeFactory INSTANCE = new ExchangeFactory(
        HttpUtil.newHttpClientBuilder(HttpUtil.newPoolingConnectionManager(50)).build());
  }

  public ExchangeFactory(CloseableHttpClient httpClient) {
    if (httpClient == null) {
      throw new NullPointerException("httpClient");
    }
    this.httpClient = httpClient;
  }

  public void setVersion(ExchangeVersion version) {
    if (version == null) {
      throw new NullPointerException("version");
    }
    this.version = version;
  }

  public void setTimeout(long timeout, TimeUnit timeUnit) {
    if (timeout <= 0) {
      throw new IllegalArgumentException("Invalid timeout");
    }
    this.timeout = (int) timeUnit.toMillis(timeout);
  }

  public ExchangeService newExchangeService() {
    return newExchangeService(version);
  }

  public ExchangeService newExchangeService(ExchangeVersion exchangeVersion) {
    ExchangeService es = new ExchangeService(httpClient, exchangeVersion);
    if (timeout != null) {
      es.setTimeout(timeout);
    }
    return es;
  }

  public CloseableHttpClient httpClient() {
    return httpClient;
  }

  public AutodiscoverService newAutodiscoverService() {
    return newAutodiscoverService(version);
  }

  public AutodiscoverService newAutodiscoverService(ExchangeVersion exchangeVersion) {
    return newAutodiscoverService(null, null, exchangeVersion);
  }

  public AutodiscoverService newAutodiscoverService(String domain) {
    return newAutodiscoverService(domain, version);
  }

  public AutodiscoverService newAutodiscoverService(String domain, ExchangeVersion exchangeVersion) {
    return newAutodiscoverService(null, domain, exchangeVersion);
  }

  public AutodiscoverService newAutodiscoverService(URI url) {
    return newAutodiscoverService(url, version);
  }

  public AutodiscoverService newAutodiscoverService(URI url, ExchangeVersion exchangeVersion) {
    return newAutodiscoverService(url, url.getHost(), exchangeVersion);
  }

  public AutodiscoverService newAutodiscoverService(URI url, String domain, ExchangeVersion exchangeVersion) {
    AutodiscoverService as = new AutodiscoverService(httpClient, url, domain, exchangeVersion);
    if (timeout != null) {
      as.setTimeout(timeout);
    }
    return as;
  }
}
