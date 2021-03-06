/***************************************************************************
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
 ***************************************************************************/
package org.exoplatform.answer.webui.popup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.exoplatform.answer.webui.BaseUIFAQForm;
import org.exoplatform.answer.webui.FAQUtils;
import org.exoplatform.commons.utils.StringCommonUtils;
import org.exoplatform.faq.service.FAQEventQuery;
import org.exoplatform.faq.service.FAQSetting;
import org.exoplatform.faq.service.ObjectSearchResult;
import org.exoplatform.forum.common.UserHelper;
import org.exoplatform.forum.common.webui.UIPopupAction;
import org.exoplatform.forum.common.webui.UIPopupContainer;
import org.exoplatform.services.resources.LocaleConfig;
import org.exoplatform.services.resources.LocaleConfigService;
import org.exoplatform.web.application.ApplicationMessage;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.core.model.SelectItemOption;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.Event.Phase;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIFormDateTimeInput;
import org.exoplatform.webui.form.UIFormRadioBoxInput;
import org.exoplatform.webui.form.UIFormSelectBox;
import org.exoplatform.webui.form.UIFormStringInput;
import org.exoplatform.webui.form.UIFormTextAreaInput;

@ComponentConfig(
    lifecycle = UIFormLifecycle.class, 
    template = "app:/templates/answer/webui/popup/UIAdvancedSearchForm.gtmpl", 
    events = {
        @EventConfig(listeners = UIAdvancedSearchForm.SearchActionListener.class), 
        @EventConfig(listeners = UIAdvancedSearchForm.OnchangeActionListener.class, phase = Phase.DECODE), 
        @EventConfig(listeners = UIAdvancedSearchForm.CancelActionListener.class, phase = Phase.DECODE) 
    }
)
public class UIAdvancedSearchForm extends BaseUIFAQForm implements UIPopupComponent {
  final static private String FIELD_TEXT                   = "Text";

  final static private String FIELD_SEARCHOBJECT_SELECTBOX = "SearchObject";

  final static private String FIELD_CATEGORY_NAME          = "CategoryName";

  final static private String FIELD_ISMODERATEQUESTION     = "IsModerateQuestion";

  final static private String FIELD_CATEGORY_MODERATOR     = "CategoryModerator";

  final static private String FIELD_FROM_DATE              = "FromDate";

  final static private String FIELD_TO_DATE                = "ToDate";

  final static private String FIELD_AUTHOR                 = "Author";

  final static private String FIELD_EMAIL_ADDRESS          = "EmailAddress";

  final static private String FIELD_LANGUAGE               = "Language";

  final static private String FIELD_QUESTION               = "Question";

  final static private String FIELD_RESPONSE               = "Response";

  final static private String FIELD_COMMENT                = "Comment";

  final static private String ITEM_EMPTY                   = "categoryAndQuestion";

  final static private String ITEM_CATEGORY                = "faqCategory";

  final static private String ITEM_QUESTION                = "faqQuestion";

  final static private String ITEM_MODERATEQUESTION_TRUE   = "true";

  final static private String ITEM_MODERATEQUESTION_FALSE  = "false";

  private FAQSetting          faqSetting_                  = new FAQSetting();

  private String              defaultLanguage_             = "";
  
  private String              type                         = "categoryAndQuestion";

  public UIAdvancedSearchForm() throws Exception {
    faqSetting_ = new FAQSetting();
    String currentUser = FAQUtils.getCurrentUser();
    FAQUtils.getPorletPreference(faqSetting_);
    if (currentUser != null && currentUser.trim().length() > 0) {
      if (faqSetting_.getIsAdmin() == null || faqSetting_.getIsAdmin().trim().length() < 1) {
        if (getFAQService().isAdminRole(null))
          faqSetting_.setIsAdmin("TRUE");
        else
          faqSetting_.setIsAdmin("FALSE");
      }
      getFAQService().getUserSetting(currentUser, faqSetting_);
    } else {
      faqSetting_.setIsAdmin("FALSE");
    }
    UIFormStringInput text = new UIFormStringInput(FIELD_TEXT, FIELD_TEXT, null);
    List<String> listLanguage = new ArrayList<String>();
    LocaleConfigService configService = getApplicationComponent(LocaleConfigService.class);
    defaultLanguage_ = configService.getDefaultLocaleConfig().getLocale().getDisplayLanguage();
    for (Object object : configService.getLocalConfigs()) {
      LocaleConfig localeConfig = (LocaleConfig) object;
      Locale locale = localeConfig.getLocale();
      String displayName = locale.getDisplayLanguage();
      listLanguage.add(displayName);
    }
    List<SelectItemOption<String>> list = new ArrayList<SelectItemOption<String>>();
    list.add(new SelectItemOption<String>(getLabel(ITEM_EMPTY), ITEM_EMPTY));
    list.add(new SelectItemOption<String>(getLabel(ITEM_CATEGORY), ITEM_CATEGORY));
    list.add(new SelectItemOption<String>(getLabel(ITEM_QUESTION), ITEM_QUESTION));
    UIFormRadioBoxInput searchType = new UIFormRadioBoxInput(FIELD_SEARCHOBJECT_SELECTBOX, FIELD_SEARCHOBJECT_SELECTBOX, list);
    searchType.setDefaultValue(ITEM_EMPTY);
    
    //searchType.setOnChange("Onchange");
    UIFormStringInput categoryName = new UIFormStringInput(FIELD_CATEGORY_NAME, FIELD_CATEGORY_NAME, null);
    list = new ArrayList<SelectItemOption<String>>();
    list.add(new SelectItemOption<String>(ITEM_MODERATEQUESTION_TRUE, "true"));
    list.add(new SelectItemOption<String>(ITEM_MODERATEQUESTION_FALSE, "false"));
    UIFormRadioBoxInput modeQuestion = new UIFormRadioBoxInput(FIELD_ISMODERATEQUESTION, FIELD_ISMODERATEQUESTION, list);
    
    UIFormStringInput moderator = new UIFormStringInput(FIELD_CATEGORY_MODERATOR, FIELD_CATEGORY_MODERATOR, null);
    UIFormDateTimeInput fromDate = new UIFormDateTimeInput(FIELD_FROM_DATE, FIELD_FROM_DATE, null, false);
    UIFormDateTimeInput toDate = new UIFormDateTimeInput(FIELD_TO_DATE, FIELD_TO_DATE, null, false);
    // search question
    UIFormStringInput author = new UIFormStringInput(FIELD_AUTHOR, FIELD_AUTHOR, null);
    UIFormStringInput emailAdress = new UIFormStringInput(FIELD_EMAIL_ADDRESS, FIELD_EMAIL_ADDRESS, null);
    list = new ArrayList<SelectItemOption<String>>();
    list.add(new SelectItemOption<String>(defaultLanguage_, defaultLanguage_));
    for (String language : listLanguage) {
      if (language.equals(defaultLanguage_))
        continue;
      list.add(new SelectItemOption<String>(language, language));
    }
    UIFormSelectBox language = new UIFormSelectBox(FIELD_LANGUAGE, FIELD_LANGUAGE, list);
    UIFormTextAreaInput question = new UIFormTextAreaInput(FIELD_QUESTION, FIELD_QUESTION, null);
    UIFormTextAreaInput response = new UIFormTextAreaInput(FIELD_RESPONSE, FIELD_RESPONSE, null);
    UIFormTextAreaInput comment = new UIFormTextAreaInput(FIELD_COMMENT, FIELD_COMMENT, null);

    addUIFormInput(text);
    addUIFormInput(searchType);
    addUIFormInput(categoryName);
    addUIFormInput(modeQuestion);
    addUIFormInput(moderator);

    addUIFormInput(author);
    addUIFormInput(emailAdress);
    addUIFormInput(language);
    addUIFormInput(question);
    addUIFormInput(response);
    addUIFormInput(comment);
    addUIFormInput(fromDate);
    addUIFormInput(toDate);
    setActions(new String[] { "Search", "Cancel" });
  }

  public void activate() {
  }

  public void deActivate() {
  }

  public Calendar getFromDate() {
    return getCalendar(getUIFormDateTimeInput(FIELD_FROM_DATE), FIELD_FROM_DATE);
  }

  public Calendar getToDate() {
    return getCalendar(getUIFormDateTimeInput(FIELD_TO_DATE), FIELD_TO_DATE);
  }

  public void setText(String value) {
    getUIStringInput(FIELD_TEXT).setValue(value);
  }

  public String getText() {
    return getUIStringInput(FIELD_TEXT).getValue();
  }
  
  public UIFormRadioBoxInput getUIFormRadioBoxInput(String name) {
    return (UIFormRadioBoxInput) findComponentById(name);
  }
  
  public void setIsSearch(boolean isCategory, boolean isQuestion) {
    UIFormStringInput categoryName = getUIStringInput(FIELD_CATEGORY_NAME).setRendered(isCategory);
    UIFormRadioBoxInput modeQuestion = getUIFormRadioBoxInput(FIELD_ISMODERATEQUESTION).setRendered(isCategory);
    UIFormStringInput moderator = getUIStringInput(FIELD_CATEGORY_MODERATOR).setRendered(isCategory);
    categoryName.setValue("");
    modeQuestion.setValue("");
    moderator.setValue("");

    UIFormStringInput author = getUIStringInput(FIELD_AUTHOR).setRendered(isQuestion);
    UIFormStringInput emailAddress = getUIStringInput(FIELD_EMAIL_ADDRESS).setRendered(isQuestion);
    UIFormSelectBox language = getUIFormSelectBox(FIELD_LANGUAGE).setRendered(isQuestion);
    UIFormTextAreaInput question = getUIFormTextAreaInput(FIELD_QUESTION).setRendered(isQuestion);
    UIFormTextAreaInput response = getUIFormTextAreaInput(FIELD_RESPONSE).setRendered(isQuestion);
    UIFormTextAreaInput comment = getUIFormTextAreaInput(FIELD_COMMENT).setRendered(isQuestion);
    author.setValue("");
    emailAddress.setValue("");
    language.setValue("");
    question.setValue("");
    response.setValue("");
    comment.setValue("");
  }

  private Calendar getCalendar(UIFormDateTimeInput dateTimeInput, String field) {
    Calendar calendar = dateTimeInput.getCalendar();
    if (!FAQUtils.isFieldEmpty(dateTimeInput.getValue())) {
      if (calendar == null) {
        warning("UIAdvancedSearchForm.msg.error-input-text-date", "UIAdvancedSearchForm.label." + field);
      }
    }
    return calendar;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  static public class OnchangeActionListener extends EventListener<UIAdvancedSearchForm> {
    public void execute(Event<UIAdvancedSearchForm> event) throws Exception {
      UIAdvancedSearchForm uiAdvancedSearchForm = event.getSource();
      String type = uiAdvancedSearchForm.getUIFormRadioBoxInput(FIELD_SEARCHOBJECT_SELECTBOX).getValue();
      uiAdvancedSearchForm.setType(type);
      if (type.equals("faqCategory")) {
        uiAdvancedSearchForm.setIsSearch(true, false);
      } else if (type.equals("faqQuestion")) {
        uiAdvancedSearchForm.setIsSearch(false, true);
      } else {
        uiAdvancedSearchForm.setIsSearch(false, false);
      }
      event.getRequestContext().addUIComponentToUpdateByAjax(uiAdvancedSearchForm);
    }
  }

  static public class SearchActionListener extends EventListener<UIAdvancedSearchForm> {
    public void execute(Event<UIAdvancedSearchForm> event) throws Exception {
      UIAdvancedSearchForm advancedSearch = event.getSource();
      /**
       * Get data from FormInput
       */
      String type = advancedSearch.getUIFormRadioBoxInput(FIELD_SEARCHOBJECT_SELECTBOX).getValue();
      String text = advancedSearch.getUIStringInput(FIELD_TEXT).getValue();
      String categoryName = advancedSearch.getUIStringInput(FIELD_CATEGORY_NAME).getValue();
      String modeQuestion = advancedSearch.getUIFormRadioBoxInput(FIELD_ISMODERATEQUESTION).getValue();
      String moderator = advancedSearch.getUIStringInput(FIELD_CATEGORY_MODERATOR).getValue();
      String author = advancedSearch.getUIStringInput(FIELD_AUTHOR).getValue();
      String emailAddress = advancedSearch.getUIStringInput(FIELD_EMAIL_ADDRESS).getValue();
      String language = advancedSearch.getUIFormSelectBox(FIELD_LANGUAGE).getValue();
      String question = advancedSearch.getUIFormTextAreaInput(FIELD_QUESTION).getValue();
      String response = advancedSearch.getUIFormTextAreaInput(FIELD_RESPONSE).getValue();
      String comment = advancedSearch.getUIFormTextAreaInput(FIELD_COMMENT).getValue();
      Calendar fromDate = advancedSearch.getFromDate();
      Calendar toDate = advancedSearch.getToDate();
      /**
       * Check validation of data inputed
       */
      if (fromDate != null && toDate != null) {
        if (fromDate.after(toDate)) {
          advancedSearch.warning("UIAdvancedSearchForm.msg.erro-from-less-than-to");
          return;
        }
        if (fromDate.equals(toDate)) {
          long timeOneDay = ((23 * 60 + 59) * 60 + 59) * 1000;
          toDate.setTimeInMillis(toDate.getTimeInMillis() +  timeOneDay) ;
        }
      }
      if (!FAQUtils.isValidEmailAddresses(emailAddress)) {
        advancedSearch.warning("UIAdvancedSearchForm.msg.email-invalid");
        return;
      }
      
      text = StringCommonUtils.encodeSpecialCharForSimpleInput(text);
      categoryName = StringCommonUtils.encodeSpecialCharInSearchTerm(categoryName);
      question = StringCommonUtils.encodeSpecialCharInSearchTerm(question);
      response = StringCommonUtils.encodeSpecialCharForSimpleInput(response);
      comment = StringCommonUtils.encodeSpecialCharForSimpleInput(comment);
      /**
       * Create query string from data inputed
       */
      FAQEventQuery eventQuery = new FAQEventQuery();
      eventQuery.setType(type);
      eventQuery.setText(text);
      eventQuery.setName(categoryName);
      eventQuery.setIsModeQuestion(modeQuestion);
      eventQuery.setModerator(moderator);
      eventQuery.setFromDate(fromDate);
      eventQuery.setToDate(toDate);
      eventQuery.setAuthor(author);
      eventQuery.setEmail(emailAddress);
      eventQuery.setAttachment("");
      eventQuery.setQuestion(question);
      eventQuery.setResponse(response);
      eventQuery.setComment(comment);
      if (language != null && language.length() > 0 && !language.equals(advancedSearch.defaultLanguage_)) {
        eventQuery.setLanguage(language);
        eventQuery.setSearchOnDefaultLanguage(false);
      } else {
        eventQuery.setLanguage(advancedSearch.defaultLanguage_);
        eventQuery.setSearchOnDefaultLanguage(true);
      }

      /**
       * Check all values are got from UIForm, if don't have any thing then view warning
       */

      String userName = FAQUtils.getCurrentUser();
      eventQuery.setUserId(userName);
      eventQuery.setUserMembers(UserHelper.getAllGroupAndMembershipOfUser(null));
      eventQuery.setAdmin(Boolean.parseBoolean(advancedSearch.faqSetting_.getIsAdmin()));

      List<ObjectSearchResult> objectSearchResults = new ArrayList<ObjectSearchResult>();
      try {
        objectSearchResults = advancedSearch.getFAQService().getSearchResults(eventQuery);
      } catch (javax.jcr.query.InvalidQueryException e) {
        event.getRequestContext()
             .getUIApplication()
             .addMessage(new ApplicationMessage("UIAdvancedSearchForm.msg.erro-empty-search", null, ApplicationMessage.WARNING));        
        return;
      }
      UIPopupContainer popupContainer = advancedSearch.getAncestorOfType(UIPopupContainer.class);
      ResultQuickSearch result = popupContainer.getChild(ResultQuickSearch.class);
      if (result == null){
        result = popupContainer.addChild(ResultQuickSearch.class, null, null);
      }
      result.setSearchResults(objectSearchResults);
      event.getRequestContext().addUIComponentToUpdateByAjax(popupContainer);
    }
  }

  static public class CancelActionListener extends EventListener<UIAdvancedSearchForm> {
    public void execute(Event<UIAdvancedSearchForm> event) throws Exception {
      UIAdvancedSearchForm advancedSearch = event.getSource();
      UIPopupAction uiPopupAction = advancedSearch.getAncestorOfType(UIPopupAction.class);
      uiPopupAction.deActivate();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPopupAction);
    }
  }
}
