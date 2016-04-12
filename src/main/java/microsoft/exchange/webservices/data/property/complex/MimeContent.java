/*
 * The MIT License
 * Copyright (c) 2012 Microsoft Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package microsoft.exchange.webservices.data.property.complex;

import com.google.common.io.ByteSource;
import microsoft.exchange.webservices.data.core.EwsServiceXmlReader;
import microsoft.exchange.webservices.data.core.EwsServiceXmlWriter;
import microsoft.exchange.webservices.data.core.XmlAttributeNames;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceXmlDeserializationException;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceXmlSerializationException;
import microsoft.exchange.webservices.data.util.CachedContent;
import org.apache.commons.codec.binary.Base64;

import javax.xml.stream.XMLStreamException;

import java.io.IOException;

/**
 * Represents the MIME content of an item.
 */
public final class MimeContent extends ComplexProperty implements AutoCloseable {

  /**
   * The character set.
   */
  private String characterSet;

  /**
   * The content.
   */
  private ByteSource content;

  // Cached content if this is part of a response
  private CachedContent cachedContent;

  /**
   * Initializes a new instance of the class.
   */
  public MimeContent() {
  }

  /**
   * Initializes a new instance of the class.
   *
   * @param characterSet the character set
   * @param content      the content
   */
  public MimeContent(String characterSet, ByteSource content) {
    this();
    this.characterSet = characterSet;
    this.content = content;
  }

  /**
   * Reads attribute from XML.
   *
   * @param reader the reader
   * @throws Exception the exception
   */
  @Override
  public void readAttributesFromXml(EwsServiceXmlReader reader)
      throws Exception {
    this.characterSet = reader.readAttributeValue(String.class,
        XmlAttributeNames.CharacterSet);
  }

  /**
   * Reads text value from XML.
   *
   * @param reader the reader
   * @throws XMLStreamException the XML stream exception
   * @throws ServiceXmlDeserializationException the service xml deserialization exception
   */
  @Override
  public void readTextValueFromXml(EwsServiceXmlReader reader)
      throws XMLStreamException, ServiceXmlDeserializationException {
    if (cachedContent != null) {
      cachedContent.delete();
    }
    try {
      cachedContent = reader.readBase64ElementValue();
    } catch (Exception e) {
      throw new ServiceXmlDeserializationException("Error reading base64 content", e);
    }
    content = cachedContent.source();
  }

  /**
   * Writes attribute to XML.
   *
   * @param writer the writer
   * @throws ServiceXmlSerializationException the service xml serialization exception
   */
  @Override
  public void writeAttributesToXml(EwsServiceXmlWriter writer)
      throws ServiceXmlSerializationException {
    writer.writeAttributeValue(XmlAttributeNames.CharacterSet,
        this.characterSet);
  }

  /**
   * Writes elements to XML.
   *
   * @param writer the writer
   * @throws ServiceXmlSerializationException the service xml serialization exception
   */
  public void writeElementsToXml(EwsServiceXmlWriter writer)
      throws ServiceXmlSerializationException {
    if (this.content != null) {
      try {
        writer.writeBase64ElementValue(this.content);
      } catch (IOException e) {
        throw new ServiceXmlSerializationException("Unable to write base64 encoded element", e);
      }
    }
  }

  /**
   * Gets  the character set of the content.
   *
   * @return the character set
   */
  public String getCharacterSet() {
    return this.characterSet;
  }

  /**
   * Sets the character set.
   *
   * @param characterSet the new character set
   */
  public void setCharacterSet(String characterSet) {
    this.canSetFieldValue(this.characterSet, characterSet);
  }

  /**
   * Gets  the character set of the content.
   *
   * @return the content
   */
  public ByteSource getContent() {
    return this.content;
  }

  /**
   * Sets the content.
   *
   * @param content the new content
   */
  public void setContent(ByteSource content) {
    this.content = content;
  }

  // Disposes of any resources associated with this object.
  @Override
  public void close() {
    if (cachedContent != null) {
      cachedContent.delete();
      cachedContent = null;
      content = null;
    }
  }
}
