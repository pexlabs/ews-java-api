package microsoft.exchange.webservices;

import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import microsoft.exchange.webservices.data.core.ExchangeFactory;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.PropertySet;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.misc.TraceFlags;
import microsoft.exchange.webservices.data.core.enumeration.property.WellKnownFolderName;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.core.service.schema.ItemSchema;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.misc.ITraceListener;
import microsoft.exchange.webservices.data.property.complex.MimeContent;
import microsoft.exchange.webservices.data.util.CachedContent;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.PrintStream;
import java.net.URI;
import java.util.EnumSet;

public class PexTest {
  private static ExchangeFactory factory = ExchangeFactory.defaultFactory();

  @Test
  public void streamingMime() throws Exception {
    ExchangeService ews = factory.newExchangeService(ExchangeVersion.Exchange2010_SP2);
    ews.setCredentials(new WebCredentials("shared.test@pexlabs.com", "pexlabs!rocks1"));
    ews.setUrl(URI.create("https://outlook.office365.com/EWS/Exchange.asmx"));
    ews.setTraceFlags(EnumSet.allOf(TraceFlags.class));
    ews.setTraceEnabled(true);
    ews.setTraceListener(new ITraceListener() {
      @Override public void trace(String traceType, String traceMessage) {
        System.out.println("[" + traceType + "]\n" + traceMessage);
      }
    });

    CachedContent content = new CachedContent();
    try (PrintStream ps = new PrintStream(content.sink().openBufferedStream())) {
      ps.println("From: Sample <sample@example.com>");
      ps.println("To: Brenda <brenda@example.com>");
      ps.println("Subject: xxx");
      ps.println("Message-ID: <f0dd5eae-219c-4b10-8c31-4840e35a97c8@eno.local>");
      ps.println("Content-Type: text/plain; charset=utf-8");
      ps.println("Content-Transfer-Encoding: 7bit");
      ps.println("MIME-Version: 1.0");
      ps.println();
      for (int i = 0; i < 300; i++) {
        ps.println(Strings.repeat("X", 132));
      }
    }
    ByteSource source = content.source();
    System.out.println("XXX size = " + source.size());
    EmailMessage msg = new EmailMessage(ews);
    msg.setMimeContent(new MimeContent("utf-8", source));
    msg.setIsRead(false);
    msg.save(WellKnownFolderName.Inbox);
    System.out.println("XXX Item id = " + msg.getId().getUniqueId());
    EmailMessage fetchedMsg = EmailMessage.bind(ews, msg.getId(), new PropertySet(ItemSchema.MimeContent));
    byte[] original = source.read();
    byte[] fetched = fetchedMsg.getMimeContent().getContent().read();
    System.out.println("XXX original size = " + original.length);
    System.out.println("XXX fetched size = " + fetched.length);
    System.out.println("XXX temp = " + System.getProperty("java.io.tmpdir"));
    fetchedMsg.close();
    content.delete();
  }
}
