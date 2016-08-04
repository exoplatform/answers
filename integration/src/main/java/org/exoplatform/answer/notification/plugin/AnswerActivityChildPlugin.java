/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU Affero General Public License
* as published by the Free Software Foundation; either version 3
* of the License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.answer.notification.plugin;

import org.exoplatform.answer.ext.activity.ForumActivityUtils;
import org.exoplatform.commons.api.notification.NotificationContext;
import org.exoplatform.commons.api.notification.model.NotificationInfo;
import org.exoplatform.commons.api.notification.plugin.AbstractNotificationChildPlugin;
import org.exoplatform.commons.api.notification.service.template.TemplateContext;
import org.exoplatform.commons.notification.template.TemplateUtils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.social.core.activity.model.ExoSocialActivity;
import org.exoplatform.social.core.manager.ActivityManager;

public class AnswerActivityChildPlugin extends AbstractNotificationChildPlugin {

  public static final String ID = "ks-answer:spaces";
  private ExoSocialActivity activity = null;

  public AnswerActivityChildPlugin(InitParams initParams) {
    super(initParams);
  }

  @Override
  public String makeContent(NotificationContext ctx) {
    try {
      NotificationInfo notification = ctx.getNotificationInfo();

      String language = getLanguage(notification);
      TemplateContext templateContext = new TemplateContext(ID, language);

      String activityId = notification.getValueOwnerParameter(ForumNotificationUtils.ACTIVITY_ID.getKey());
      ActivityManager activityManager = ForumActivityUtils.getActivityManager();
      activity = activityManager.getActivity(activityId);
      String activityUrl;
      if (activity.isComment()) {
        activity = activityManager.getParentActivity(activity);
        activityUrl = CommonsUtils.getCurrentDomain() + activity.getTemplateParams().get("Link");
      } else {
        activityUrl = activity.getTemplateParams().get("Link");
      }
      templateContext.put("ACTIVITY", activity.getTitle());
      templateContext.put("ACTIVITY_URL", activityUrl);
      //
      String content = TemplateUtils.processGroovy(templateContext);
      return content;
    } catch (Exception e) {
      return (activity != null) ? activity.getTitle() : "";
    }
  }

  public String getActivityParamValue(String key) {
    return ForumNotificationUtils.getActivityParamValue(activity, key);
  }

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public boolean isValid(NotificationContext ctx) {
    return false;
  }

}
