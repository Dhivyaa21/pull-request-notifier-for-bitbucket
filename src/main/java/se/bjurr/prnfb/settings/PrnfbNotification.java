package se.bjurr.prnfb.settings;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.util.regex.Pattern.compile;
import static se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD.GET;
import static se.bjurr.prnfb.settings.TRIGGER_IF_MERGE.ALWAYS;

import java.net.URL;
import java.util.List;
import java.util.UUID;

import se.bjurr.prnfb.http.UrlInvoker.HTTP_METHOD;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;

import com.atlassian.bitbucket.pull.PullRequestState;
import com.google.common.base.Optional;

public class PrnfbNotification implements HasUuid {

 private static final String DEFAULT_NAME = "Notification";
 private final UUID uuid;
 private final String filterRegexp;
 private final String filterString;
 private final String password;
 private final List<PrnfbPullRequestAction> triggers;
 private final String url;
 private final String user;
 private final HTTP_METHOD method;
 private final String postContent;
 private final List<PrnfbHeader> headers;
 private final String proxyUser;
 private final String proxyPassword;
 private final String proxyServer;
 private final Integer proxyPort;
 private final String name;
 private final String injectionUrl;
 private final String injectionUrlRegexp;
 private final TRIGGER_IF_MERGE triggerIfCanMerge;
 private final List<PullRequestState> triggerIgnoreStateList;

 public PrnfbNotification(PrnfbNotificationBuilder builder) throws ValidationException {
  this.uuid = builder.getUUID();
  this.proxyUser = emptyToNull(nullToEmpty(builder.getProxyUser()).trim());
  this.proxyPassword = emptyToNull(nullToEmpty(builder.getProxyPassword()).trim());
  this.proxyServer = emptyToNull(nullToEmpty(builder.getProxyServer()).trim());
  this.proxyPort = firstNonNull(builder.getProxyPort(), -1);
  this.headers = checkNotNull(builder.getHeaders());
  this.postContent = emptyToNull(nullToEmpty(builder.getPostContent()).trim());
  this.method = firstNonNull(builder.getMethod(), GET);
  this.triggerIfCanMerge = firstNonNull(builder.getTriggerIfCanMerge(), ALWAYS);
  try {
   new URL(builder.getUrl());
  } catch (final Exception e) {
   throw new ValidationException("url", "URL not valid!");
  }
  if (!nullToEmpty(builder.getFilterRegexp()).trim().isEmpty()) {
   try {
    compile(builder.getFilterRegexp());
   } catch (final Exception e) {
    throw new ValidationException("filter_regexp", "Filter regexp not valid! " + e.getMessage().replaceAll("\n", " "));
   }
   if (nullToEmpty(builder.getFilterString()).trim().isEmpty()) {
    throw new ValidationException("filter_string", "Filter string not set, nothing to match regexp against!");
   }
  }
  this.url = builder.getUrl();
  this.user = emptyToNull(nullToEmpty(builder.getUser()).trim());
  this.password = emptyToNull(nullToEmpty(builder.getPassword()).trim());
  this.triggers = checkNotNull(builder.getTriggers());
  this.filterString = builder.getFilterString();
  this.filterRegexp = builder.getFilterRegexp();
  this.name = firstNonNull(emptyToNull(nullToEmpty(builder.getName()).trim()), DEFAULT_NAME);
  this.injectionUrl = emptyToNull(nullToEmpty(builder.getInjectionUrl()).trim());
  this.injectionUrlRegexp = emptyToNull(nullToEmpty(builder.getInjectionUrlRegexp()).trim());
  this.triggerIgnoreStateList = builder.getTriggerIgnoreStateList();
 }

 public List<PullRequestState> getTriggerIgnoreStateList() {
  return triggerIgnoreStateList;
 }

 public TRIGGER_IF_MERGE getTriggerIfCanMerge() {
  return triggerIfCanMerge;
 }

 public Optional<String> getFilterRegexp() {
  return fromNullable(filterRegexp);
 }

 public Optional<String> getFilterString() {
  return fromNullable(filterString);
 }

 public Optional<String> getPassword() {
  return fromNullable(password);
 }

 public Optional<String> getProxyPassword() {
  return fromNullable(proxyPassword);
 }

 public Integer getProxyPort() {
  return proxyPort;
 }

 public Optional<String> getProxyServer() {
  return fromNullable(proxyServer);
 }

 public Optional<String> getProxyUser() {
  return fromNullable(proxyUser);
 }

 public String getName() {
  return name;
 }

 public List<PrnfbPullRequestAction> getTriggers() {
  return triggers;
 }

 public String getUrl() {
  return url;
 }

 public Optional<String> getUser() {
  return fromNullable(user);
 }

 public HTTP_METHOD getMethod() {
  return method;
 }

 public Optional<String> getPostContent() {
  return fromNullable(postContent);
 }

 public List<PrnfbHeader> getHeaders() {
  return headers;
 }

 public Optional<String> getInjectionUrl() {
  return fromNullable(injectionUrl);
 }

 public Optional<String> getInjectionUrlRegexp() {
  return fromNullable(injectionUrlRegexp);
 }

 @Override
 public UUID getUuid() {
  return uuid;
 }

 @Override
 public int hashCode() {
  final int prime = 31;
  int result = 1;
  result = prime * result + ((filterRegexp == null) ? 0 : filterRegexp.hashCode());
  result = prime * result + ((filterString == null) ? 0 : filterString.hashCode());
  result = prime * result + ((headers == null) ? 0 : headers.hashCode());
  result = prime * result + ((injectionUrl == null) ? 0 : injectionUrl.hashCode());
  result = prime * result + ((injectionUrlRegexp == null) ? 0 : injectionUrlRegexp.hashCode());
  result = prime * result + ((method == null) ? 0 : method.hashCode());
  result = prime * result + ((name == null) ? 0 : name.hashCode());
  result = prime * result + ((password == null) ? 0 : password.hashCode());
  result = prime * result + ((postContent == null) ? 0 : postContent.hashCode());
  result = prime * result + ((proxyPassword == null) ? 0 : proxyPassword.hashCode());
  result = prime * result + ((proxyPort == null) ? 0 : proxyPort.hashCode());
  result = prime * result + ((proxyServer == null) ? 0 : proxyServer.hashCode());
  result = prime * result + ((proxyUser == null) ? 0 : proxyUser.hashCode());
  result = prime * result + ((triggerIfCanMerge == null) ? 0 : triggerIfCanMerge.hashCode());
  result = prime * result + ((triggerIgnoreStateList == null) ? 0 : triggerIgnoreStateList.hashCode());
  result = prime * result + ((triggers == null) ? 0 : triggers.hashCode());
  result = prime * result + ((url == null) ? 0 : url.hashCode());
  result = prime * result + ((user == null) ? 0 : user.hashCode());
  result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
  return result;
 }

 @Override
 public boolean equals(Object obj) {
  if (this == obj) {
   return true;
  }
  if (obj == null) {
   return false;
  }
  if (getClass() != obj.getClass()) {
   return false;
  }
  PrnfbNotification other = (PrnfbNotification) obj;
  if (filterRegexp == null) {
   if (other.filterRegexp != null) {
    return false;
   }
  } else if (!filterRegexp.equals(other.filterRegexp)) {
   return false;
  }
  if (filterString == null) {
   if (other.filterString != null) {
    return false;
   }
  } else if (!filterString.equals(other.filterString)) {
   return false;
  }
  if (headers == null) {
   if (other.headers != null) {
    return false;
   }
  } else if (!headers.equals(other.headers)) {
   return false;
  }
  if (injectionUrl == null) {
   if (other.injectionUrl != null) {
    return false;
   }
  } else if (!injectionUrl.equals(other.injectionUrl)) {
   return false;
  }
  if (injectionUrlRegexp == null) {
   if (other.injectionUrlRegexp != null) {
    return false;
   }
  } else if (!injectionUrlRegexp.equals(other.injectionUrlRegexp)) {
   return false;
  }
  if (method != other.method) {
   return false;
  }
  if (name == null) {
   if (other.name != null) {
    return false;
   }
  } else if (!name.equals(other.name)) {
   return false;
  }
  if (password == null) {
   if (other.password != null) {
    return false;
   }
  } else if (!password.equals(other.password)) {
   return false;
  }
  if (postContent == null) {
   if (other.postContent != null) {
    return false;
   }
  } else if (!postContent.equals(other.postContent)) {
   return false;
  }
  if (proxyPassword == null) {
   if (other.proxyPassword != null) {
    return false;
   }
  } else if (!proxyPassword.equals(other.proxyPassword)) {
   return false;
  }
  if (proxyPort == null) {
   if (other.proxyPort != null) {
    return false;
   }
  } else if (!proxyPort.equals(other.proxyPort)) {
   return false;
  }
  if (proxyServer == null) {
   if (other.proxyServer != null) {
    return false;
   }
  } else if (!proxyServer.equals(other.proxyServer)) {
   return false;
  }
  if (proxyUser == null) {
   if (other.proxyUser != null) {
    return false;
   }
  } else if (!proxyUser.equals(other.proxyUser)) {
   return false;
  }
  if (triggerIfCanMerge != other.triggerIfCanMerge) {
   return false;
  }
  if (triggerIgnoreStateList == null) {
   if (other.triggerIgnoreStateList != null) {
    return false;
   }
  } else if (!triggerIgnoreStateList.equals(other.triggerIgnoreStateList)) {
   return false;
  }
  if (triggers == null) {
   if (other.triggers != null) {
    return false;
   }
  } else if (!triggers.equals(other.triggers)) {
   return false;
  }
  if (url == null) {
   if (other.url != null) {
    return false;
   }
  } else if (!url.equals(other.url)) {
   return false;
  }
  if (user == null) {
   if (other.user != null) {
    return false;
   }
  } else if (!user.equals(other.user)) {
   return false;
  }
  if (uuid == null) {
   if (other.uuid != null) {
    return false;
   }
  } else if (!uuid.equals(other.uuid)) {
   return false;
  }
  return true;
 }

 @Override
 public String toString() {
  return "PrnfbNotification [uuid=" + uuid + ", filterRegexp=" + filterRegexp + ", filterString=" + filterString
    + ", password=" + password + ", triggers=" + triggers + ", url=" + url + ", user=" + user + ", method=" + method
    + ", postContent=" + postContent + ", headers=" + headers + ", proxyUser=" + proxyUser + ", proxyPassword="
    + proxyPassword + ", proxyServer=" + proxyServer + ", proxyPort=" + proxyPort + ", name=" + name
    + ", injectionUrl=" + injectionUrl + ", injectionUrlRegexp=" + injectionUrlRegexp + ", triggerIfCanMerge="
    + triggerIfCanMerge + ", triggerIgnoreStateList=" + triggerIgnoreStateList + "]";
 }

}
