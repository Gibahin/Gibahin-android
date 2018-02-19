package org.thoughtcrime.securesms.mms;

import android.text.TextUtils;

import org.thoughtcrime.securesms.attachments.Attachment;
import org.thoughtcrime.securesms.recipients.Recipients;

import java.util.List;

public class OutgoingMediaMessage {

  private   final Recipients       recipients;
  protected final String           body;
  protected final String           replyBody;
  protected final List<Attachment> attachments;
  private   final long             sentTimeMillis;
  private   final int              distributionType;
  private   final int              subscriptionId;
  private   final long             expiresIn;

  public OutgoingMediaMessage(Recipients recipients, String message, String replyBody,
                              List<Attachment> attachments, long sentTimeMillis,
                              int subscriptionId, long expiresIn,
                              int distributionType)
  {
    this.recipients       = recipients;
    this.body             = message;
    this.replyBody        = replyBody;
    this.sentTimeMillis   = sentTimeMillis;
    this.distributionType = distributionType;
    this.attachments      = attachments;
    this.subscriptionId   = subscriptionId;
    this.expiresIn        = expiresIn;
  }

  public OutgoingMediaMessage(Recipients recipients, String message,
                              List<Attachment> attachments, long sentTimeMillis,
                              int subscriptionId, long expiresIn,
                              int distributionType)
  {
    this(recipients,
            message,
            null,
            attachments,
            sentTimeMillis,
            subscriptionId,
            expiresIn,
            distributionType);
  }

  public OutgoingMediaMessage(Recipients recipients, SlideDeck slideDeck, String message, String replyBody, long sentTimeMillis, int subscriptionId, long expiresIn, int distributionType)
  {
    this(recipients,
         buildMessage(slideDeck, message),
         replyBody,
         slideDeck.asAttachments(),
         sentTimeMillis, subscriptionId,
         expiresIn, distributionType);
  }

  public OutgoingMediaMessage(OutgoingMediaMessage that) {
    this.recipients       = that.getRecipients();
    this.body             = that.body;
    this.replyBody        = that.replyBody;
    this.distributionType = that.distributionType;
    this.attachments      = that.attachments;
    this.sentTimeMillis   = that.sentTimeMillis;
    this.subscriptionId   = that.subscriptionId;
    this.expiresIn        = that.expiresIn;
  }

  public Recipients getRecipients() {
    return recipients;
  }

  public String getBody() {
    return body;
  }

  public String getReplyBody() {
    return replyBody;
  }

  public List<Attachment> getAttachments() {
    return attachments;
  }

  public int getDistributionType() {
    return distributionType;
  }

  public boolean isSecure() {
    return false;
  }

  public boolean isGroup() {
    return false;
  }

  public boolean isExpirationUpdate() {
    return false;
  }

  public long getSentTimeMillis() {
    return sentTimeMillis;
  }

  public int getSubscriptionId() {
    return subscriptionId;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  private static String buildMessage(SlideDeck slideDeck, String message) {
    if (!TextUtils.isEmpty(message) && !TextUtils.isEmpty(slideDeck.getBody())) {
      return slideDeck.getBody() + "\n\n" + message;
    } else if (!TextUtils.isEmpty(message)) {
      return message;
    } else {
      return slideDeck.getBody();
    }
  }
}
