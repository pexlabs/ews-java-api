package microsoft.exchange.webservices.data.util;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.FileBackedOutputStream;

import java.io.IOException;
import java.io.OutputStream;

public class CachedContent {
  private final FileBackedOutputStream os;

  public static final int THRESHOLD = 1024 * 1024;

  public CachedContent() {
    this(THRESHOLD);
  }

  public CachedContent(int threshold) {
    os = new FileBackedOutputStream(threshold, true);
  }

  public ByteSink sink() {
    return new ByteSink() {
      @Override public OutputStream openStream() throws IOException {
        os.reset();
        return os;
      }
    };
  }

  public ByteSource source() {
    return os.asByteSource();
  }

  public void delete() {
    try {
      os.reset();
    } catch (IOException e) {
      // Ignore...
    }
  }
}
