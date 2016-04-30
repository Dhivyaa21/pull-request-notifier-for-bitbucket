package se.bjurr.prnfb.service;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Ordering.usingToString;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static se.bjurr.prnfb.listener.PrnfbPullRequestAction.BUTTON_TRIGGER;
import static se.bjurr.prnfb.service.PrnfbVariable.BUTTON_TRIGGER_TITLE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import se.bjurr.prnfb.http.ClientKeyStore;
import se.bjurr.prnfb.listener.PrnfbPullRequestAction;
import se.bjurr.prnfb.listener.PrnfbPullRequestEventListener;
import se.bjurr.prnfb.settings.PrnfbButton;
import se.bjurr.prnfb.settings.PrnfbNotification;
import se.bjurr.prnfb.settings.PrnfbSettingsData;

import com.atlassian.bitbucket.pull.PullRequest;
import com.atlassian.bitbucket.pull.PullRequestService;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public class ButtonsService {

 private final PullRequestService pullRequestService;
 private final PrnfbPullRequestEventListener prnfbPullRequestEventListener;
 private final SettingsService settingsService;
 private final UserCheckService userCheckService;
 private final PrnfbRendererFactory prnfbRendererFactory;

 public ButtonsService(PullRequestService pullRequestService,
   PrnfbPullRequestEventListener prnfbPullRequestEventListener, PrnfbRendererFactory prnfbRendererFactory,
   SettingsService settingsService, UserCheckService userCheckService) {
  this.pullRequestService = pullRequestService;
  this.prnfbPullRequestEventListener = prnfbPullRequestEventListener;
  this.prnfbRendererFactory = prnfbRendererFactory;
  this.settingsService = settingsService;
  this.userCheckService = userCheckService;
 }

 public List<PrnfbButton> getButtons(Integer repositoryId, Long pullRequestId) {
  final PrnfbSettingsData settings = settingsService.getPrnfbSettingsData();
  List<PrnfbNotification> notifications = settingsService.getNotifications();
  ClientKeyStore clientKeyStore = new ClientKeyStore(settings);
  final PullRequest pullRequest = pullRequestService.getById(repositoryId, pullRequestId);
  boolean shouldAcceptAnyCertificate = settings.isShouldAcceptAnyCertificate();
  return doGetButtons(notifications, clientKeyStore, pullRequest, shouldAcceptAnyCertificate);
 }

 public void handlePressed(Integer repositoryId, Long pullRequestId, UUID buttonUuid) {
  final PrnfbSettingsData prnfbSettingsData = settingsService.getPrnfbSettingsData();
  ClientKeyStore clientKeyStore = new ClientKeyStore(prnfbSettingsData);
  boolean shouldAcceptAnyCertificate = prnfbSettingsData.isShouldAcceptAnyCertificate();
  final PullRequest pullRequest = pullRequestService.getById(repositoryId, pullRequestId);
  doHandlePressed(buttonUuid, clientKeyStore, shouldAcceptAnyCertificate, pullRequest);
 }

 @VisibleForTesting
 List<PrnfbButton> doGetButtons(List<PrnfbNotification> notifications, ClientKeyStore clientKeyStore,
   final PullRequest pullRequest, boolean shouldAcceptAnyCertificate) {
  List<PrnfbButton> allFoundButtons = newArrayList();
  for (PrnfbButton candidate : settingsService.getButtons()) {
   Map<PrnfbVariable, Supplier<String>> variables = getVariables(candidate.getUuid());
   PrnfbPullRequestAction pullRequestAction = BUTTON_TRIGGER;
   if (userCheckService.isAllowedUseButton(candidate)
     && isTriggeredByAction(clientKeyStore, notifications, shouldAcceptAnyCertificate, pullRequestAction, pullRequest,
       variables)) {
    allFoundButtons.add(candidate);
   }
  }
  allFoundButtons = usingToString().sortedCopy(allFoundButtons);
  return allFoundButtons;
 }

 private boolean isTriggeredByAction(ClientKeyStore clientKeyStore, List<PrnfbNotification> notifications,
   boolean shouldAcceptAnyCertificate, PrnfbPullRequestAction pullRequestAction, PullRequest pullRequest,
   Map<PrnfbVariable, Supplier<String>> variables) {
  for (PrnfbNotification prnfbNotification : notifications) {
   PrnfbRenderer renderer = prnfbRendererFactory.create(pullRequest, pullRequestAction, prnfbNotification, variables);
   if (prnfbPullRequestEventListener.isNotificationTriggeredByAction(prnfbNotification, pullRequestAction, renderer,
     pullRequest, clientKeyStore, shouldAcceptAnyCertificate)) {
    return TRUE;
   }
  }
  return FALSE;
 }

 @VisibleForTesting
 void doHandlePressed(UUID buttonUuid, ClientKeyStore clientKeyStore, boolean shouldAcceptAnyCertificate,
   final PullRequest pullRequest) {
  Map<PrnfbVariable, Supplier<String>> variables = getVariables(buttonUuid);
  for (PrnfbNotification prnfbNotification : settingsService.getNotifications()) {
   PrnfbPullRequestAction pullRequestAction = BUTTON_TRIGGER;
   PrnfbRenderer renderer = prnfbRendererFactory.create(pullRequest, pullRequestAction, prnfbNotification, variables);
   if (prnfbPullRequestEventListener.isNotificationTriggeredByAction(prnfbNotification, pullRequestAction, renderer,
     pullRequest, clientKeyStore, shouldAcceptAnyCertificate)) {
    prnfbPullRequestEventListener.notify(prnfbNotification, pullRequestAction, pullRequest, variables, renderer,
      clientKeyStore, shouldAcceptAnyCertificate);
   }
  }
 }

 @VisibleForTesting
 Map<PrnfbVariable, Supplier<String>> getVariables(final UUID uuid) {
  Map<PrnfbVariable, Supplier<String>> variables = new HashMap<PrnfbVariable, Supplier<String>>();
  PrnfbButton button = settingsService.getButton(uuid);
  variables.put(BUTTON_TRIGGER_TITLE, Suppliers.ofInstance(button.getTitle()));
  return variables;
 }

}
