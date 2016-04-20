package de.mpg.imeji.presentation.beans;

import java.util.Locale;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import de.mpg.imeji.logic.vo.User;

/**
 * This bean is a utility Bean. It can be extended to get some basic session information
 * 
 * @author bastiens
 *
 */
@ManagedBean(name = "SuperViewBean")
@ViewScoped
public class SuperViewBean {
  @ManagedProperty(value = "#{SessionBean.user}")
  private User sessionUser;
  @ManagedProperty(value = "#{SessionBean.locale}")
  private Locale locale;
  @ManagedProperty(value = "#{SessionBean.selectedSpaceString}")
  private String space;
  @ManagedProperty(value = "#{SessionBean.spaceId}")
  private String spaceId;
  @ManagedProperty(value = "#{Navigation}")
  private Navigation navigation;


  /**
   * @return the navigation
   */
  public Navigation getNavigation() {
    return navigation;
  }

  /**
   * @param navigation the navigation to set
   */
  public void setNavigation(Navigation navigation) {
    this.navigation = navigation;
  }

  /**
   * @return the space
   */
  public String getSpace() {
    return space;
  }

  /**
   * @param space the space to set
   */
  public void setSpace(String space) {
    this.space = space;
  }

  /**
   * @return the sessionUser
   */
  public User getSessionUser() {
    return sessionUser;
  }

  /**
   * @param sessionUser the sessionUser to set
   */
  public void setSessionUser(User sessionUser) {
    this.sessionUser = sessionUser;
  }

  /**
   * @return the locale
   */
  public Locale getLocale() {
    return locale;
  }

  /**
   * @param locale the locale to set
   */
  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  /**
   * @return the spaceId
   */
  public String getSpaceId() {
    return spaceId;
  }

  /**
   * @param spaceId the spaceId to set
   */
  public void setSpaceId(String spaceId) {
    this.spaceId = spaceId;
  }
}
