/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
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
package org.exoplatform.faq.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.exoplatform.forum.common.CommonUtils;
import org.exoplatform.forum.common.jcr.KSDataLocation;

public class Utils {
  final public static String FAQ_APP              = "faqApp".intern();

  final public static String DEFAULT_AVATAR_URL   = "/eXoSkin/skin/images/system/UserAvtDefault.png";

  final public static String QUESTION_HOME        = "questions".intern();

  final public static String CATEGORY_HOME        = KSDataLocation.Locations.FAQ_CATEGORIES_HOME;

  final public static String ANSWER_HOME          = "faqAnswerHome".intern();

  final public static String COMMENT_HOME         = "faqCommentHome".intern();

  final public static String LANGUAGE_HOME        = "languages".intern();

  final public static String ALL                  = "All".intern();

  public static final String CATE_SPACE_ID_PREFIX = "CategorySpace".intern();

  final public static String UI_FAQ_VIEWER        = "UIFAQViewer".intern();

  final public static String DELETED              = ":deleted".intern();

  final public static String HYPHEN               = "-".intern();

  final public static String QUESTION_ID_PARAM    = "questionId";

  final public static String ANSWER_NOW_PARAM     = "answer-now";

  final public static String QUESTION_ID          = String.format("?&%s=", QUESTION_ID_PARAM);

  final public static String ANSWER_NOW           = String.format("&%s=", ANSWER_NOW_PARAM);
  
  final public static String HIGHLIGHT_PATTERN    = "(.*)<strong>(.*)</strong>(.*)";
  
  /**
   * This method sort list category is date ascending
   * @author Administrator
   *
   */
  static public class DatetimeComparatorASC implements Comparator<Object> {
    public int compare(Object o1, Object o2) throws ClassCastException {
      Date date1 = ((Category) o1).getCreatedDate();
      Date date2 = ((Category) o2).getCreatedDate();
      return date1.compareTo(date2);
    }
  }

  /**
   * This method sort list category is name ascending
   * @author Administrator
   *
   */
  static public class NameComparatorASC implements Comparator<Object> {
    public int compare(Object o1, Object o2) throws ClassCastException {
      String name1 = ((Category) o1).getName();
      String name2 = ((Category) o2).getName();
      return name1.compareToIgnoreCase(name2);
    }
  }

  /**
   * This method sort list category is date descending
   * @author Administrator
   *
   */
  static public class DatetimeComparatorDESC implements Comparator<Object> {
    public int compare(Object o1, Object o2) throws ClassCastException {
      Date date1 = ((Category) o1).getCreatedDate();
      Date date2 = ((Category) o2).getCreatedDate();
      return date2.compareTo(date1);
    }
  }

  /**
   * This method sort list category is name descending
   * @author Administrator
   *
   */
  static public class NameComparatorDESC implements Comparator<Object> {
    public int compare(Object o1, Object o2) throws ClassCastException {
      String name1 = ((Category) o1).getName();
      String name2 = ((Category) o2).getName();
      return name2.compareToIgnoreCase(name1);
    }
  }

  static public boolean hasPermission(List<String> listPlugin, List<String> listOfUser) {
    List<String> groups = new ArrayList<String>();
    List<String> groupsAllmembershipType = new ArrayList<String>();
    for (String str : listOfUser) {
      if(str.indexOf("/") >= 0) {
        groups.add(str.substring(str.indexOf("/")));
      }
      if (str.indexOf("*") >= 0) {// user has membershipType *
        str = str.substring(str.indexOf("/"));
        groupsAllmembershipType.add(str);
      }
      if (listPlugin.contains(str)) {
        return true;
      }
    }
    if(groups.size() > 0 || groupsAllmembershipType.size() > 0) {
      for (String str : listPlugin) {
        if (str.indexOf("*") >= 0) {// listPlugin has membershipType * 
          str = str.substring(str.indexOf("/"));
          if(groups.contains(str)){
            return true;
          }
        }
        if(str.indexOf(":") > 0) {
          str = str.substring(str.indexOf("/"));
        }
        if(groupsAllmembershipType.contains(str)) {
          return true;
        }
      }
    }
    return false;
  }

  static public class NameComparator implements Comparator<Object> {
    public int compare(Object o1, Object o2) throws ClassCastException {
      String name1 = ((Watch) o1).getUser();
      String name2 = ((Watch) o2).getUser();
      return name1.compareToIgnoreCase(name2);
    }
  }

  public static long getTimeOfLastActivity(String info) {
    if (info == null || info.length() == 0)
      return -1;
    int dashIndex = info.lastIndexOf(HYPHEN);
    if (dashIndex < 0) {
      return -1;
    }
    try {
      return Long.parseLong(info.substring(dashIndex + 1));
    } catch (NumberFormatException nfe) {
      return -1;
    }
  }

  public static String getAuthorOfLastActivity(String info) {
    if (info == null || info.length() == 0)
      return null;
    int dashIndex = info.lastIndexOf(HYPHEN);
    if (dashIndex < 0) {
      return null;
    }
    return info.substring(0, dashIndex);
  }

  public static String getOderBy(FAQSetting faqSetting) {
    StringBuffer queryString = new StringBuffer();
    if (faqSetting.isSortQuestionByVote()) {
      queryString.append(FAQNodeTypes.AT).append(FAQNodeTypes.EXO_MARK_VOTE).append(FAQSetting.ORDERBY_DESC).append(", ");
    }
    // order by and ascending or descending
    if (faqSetting.getOrderBy().equals(FAQSetting.DISPLAY_TYPE_POSTDATE)) {
      queryString.append(FAQNodeTypes.AT).append(FAQNodeTypes.EXO_CREATED_DATE);
    } else {
      queryString.append(FAQNodeTypes.AT).append(FAQNodeTypes.EXO_TITLE);
    }
    if (faqSetting.getOrderType().equals(FAQSetting.ORDERBY_TYPE_ASC)) {
      queryString.append(FAQSetting.ORDERBY_ASC);
    } else {
      queryString.append(FAQSetting.ORDERBY_DESC);
    }
    return queryString.toString();
  }

  /**
   * Build Xpath query for check has property existing.
   * @param property the property of node
   * @return String
   */
  public static String buildXpathHasProperty(String property) {
    StringBuilder builder = new StringBuilder();
    if (CommonUtils.isEmpty(property) == false) {
      builder.append("(not(@").append(property).append(") or @")
             .append(property).append("='' or @")
             .append(property).append("=' ')");
    }
    return builder.toString();
  }

  public static String buildQueryListOfUser(String property, List<String> listOfUser) {
    StringBuilder query = new StringBuilder();
    for (String expr : listOfUser) {
      if(query.length() > 0) {
        query.append(" or ");
      }
      query.append("@").append(property).append(" = '").append(expr).append("'");
      if (FAQServiceUtils.isGroupExpression(expr)) {
        query.append(" or @").append(property).append(" = '*:").append(expr).append("'");
      } else if(FAQServiceUtils.isMembershipExpression(expr)){
        expr = expr.substring(expr.indexOf(":")+1);
        query.append(" or @").append(property).append(" = '*:").append(expr).append("'");
      }
    }
    return query.toString();
  }
  
  /**
   * Gets the Question point. It's the sum of all its answer's mark votes
   * @param question the question object
   * @return sum of all answer's mark votes of provided question.
   * @since 4.1.0
   */
  public static int getQuestionPoint(Question question) {
    //
    if (question.getAnswers() == null) return 0;

    //
    int questionPoint = 0;
    for (Answer answer : question.getAnswers()) {
      if (answer != null) {
        questionPoint += answer.getMarkVotes();
      }
    }

    return questionPoint;
  }
}
