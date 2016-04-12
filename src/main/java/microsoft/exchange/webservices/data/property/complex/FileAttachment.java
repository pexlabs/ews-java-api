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
import microsoft.exchange.webservices.data.core.EwsUtilities;
import microsoft.exchange.webservices.data.core.XmlElementNames;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.misc.XmlNamespace;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceValidationException;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceVersionException;

import microsoft.exchange.webservices.data.util.CachedContent;

/**
 * Represents a file attachment.
 */
public final class FileAttachment extends Attachment {
  /**
   * The content
   */
  private ByteSource content;

  // The cached content if this is part of a response
  private CachedContent cachedContent;

  /**
   * The is contact photo.
   */
  private boolean isContactPhoto;

  /**
   * Initializes a new instance.
   *
   * @param owner the owner
   */
  protected FileAttachment(Item owner) {
    super(owner);
  }

  /**
   * Gets the name of the XML element.
   *
   * @return XML element name
   */
  public String getXmlElementName() {
    return XmlElementNames.FileAttachment;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validate(int attachmentIndex) throws ServiceValidationException {
    if (this.content == null) {
      throw new ServiceValidationException(String.format(
          "The content of the file attachment at index %d must be set.",
          attachmentIndex));
    }
  }

  /**
   * Tries to read element from XML.
   *
   * @param reader the reader
   * @return True if element was read.
   * @throws Exception the exception
   */
  @Override
  public boolean tryReadElementFromXml(EwsServiceXmlReader reader)
      throws Exception {
    boolean result = super.tryReadElementFromXml(reader);

    if (!result) {
      if (reader.getLocalName().equals(XmlElementNames.IsContactPhoto)) {
        this.isContactPhoto = reader.readElementValue(Boolean.class);
      } else if (reader.getLocalName().equals(XmlElementNames.Content)) {
        if (cachedContent != null) {
          cachedContent.delete();
        }
        cachedContent = reader.readBase64ElementValue();
        content = cachedContent.source();
        result = true;
      }
    }
    return result;
  }


  /**
   * For FileAttachment, the only thing need to patch is the AttachmentId.
   *
   * @param reader The reader.
   * @return true if element was read
   */
  @Override
  public boolean tryReadElementFromXmlToPatch(EwsServiceXmlReader reader) throws Exception {
    return super.tryReadElementFromXml(reader);
  }


  /**
   * Writes elements and content to XML.
   *
   * @param writer the writer
   * @throws Exception the exception
   */
  @Override
  public void writeElementsToXml(EwsServiceXmlWriter writer)
      throws Exception {
    if (content == null) {
      EwsUtilities.ewsAssert(
          false, "FileAttachment.WriteElementsToXml", "The attachment's content is not set.");
    }
    super.writeElementsToXml(writer);
    // ExchangeVersion ev=writer.getService().getRequestedServerVersion();
    if (writer.getService().getRequestedServerVersion().ordinal() >
        ExchangeVersion.Exchange2007_SP1
            .ordinal()) {
      writer.writeElementValue(XmlNamespace.Types,
          XmlElementNames.IsContactPhoto, this.isContactPhoto);
    }
    writer.writeStartElement(XmlNamespace.Types, XmlElementNames.Content);
    writer.writeBase64ElementValue(content);
    writer.writeEndElement();
  }

  /**
   * Gets the content of the attachment into memory. Content is set only
   * when Load() is called.
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
  protected void setContent(ByteSource content) {
    this.throwIfThisIsNotNew();

    if (cachedContent != null) {
      cachedContent.delete();
    }
    this.content = content;
  }

  /**
   * Gets  a value indicating whether this attachment is a contact
   * photo.
   *
   * @return true, if is contact photo
   * @throws ServiceVersionException the service version exception
   */
  public boolean isContactPhoto() throws ServiceVersionException {
    EwsUtilities.validatePropertyVersion(this.getOwner().getService(),
        ExchangeVersion.Exchange2010, "IsContactPhoto");
    return this.isContactPhoto;
  }

  /**
   * Sets the checks if is contact photo.
   *
   * @param isContactPhoto the new checks if is contact photo
   * @throws ServiceVersionException the service version exception
   */
  public void setIsContactPhoto(boolean isContactPhoto)
      throws ServiceVersionException {
    EwsUtilities.validatePropertyVersion(this.getOwner().getService(),
        ExchangeVersion.Exchange2010, "IsContactPhoto");
    this.throwIfThisIsNotNew();
    this.isContactPhoto = isContactPhoto;
  }

  @Override public void close() throws Exception {
    if (cachedContent != null) {
      cachedContent.delete();
      cachedContent = null;
      content = null;
    }

  }
}
