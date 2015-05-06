/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.collection;

import de.mpg.imeji.exceptions.ImejiException;
import de.mpg.imeji.logic.controller.CollectionController;
import de.mpg.imeji.logic.controller.UserController;
import de.mpg.imeji.logic.util.ObjectHelper;
import de.mpg.imeji.logic.util.UrlHelper;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Organization;
import de.mpg.imeji.logic.vo.Person;
import de.mpg.imeji.logic.vo.User;
import de.mpg.imeji.presentation.beans.Navigation;
import de.mpg.imeji.presentation.upload.IngestImage;
import de.mpg.imeji.presentation.util.BeanHelper;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

@ManagedBean(name = "EditCollectionBean")
@SessionScoped
public class EditCollectionBean extends CollectionBean
{
    private static final long serialVersionUID = 568267990816647451L;


   	public EditCollectionBean()
    {
        super();
    }

    public void init() throws Exception
    {
        super.setTab(TabType.COLLECTION);
        //sets explicitely Edit mode for the collection Bean, to avoid classname comparisons
        setCollectionCreateMode(false);
//        
//        //retrieves the parameter with which edit collection Bean is set-up
        getProfileSelect();
        String id = super.getId();
        if (id != null)
        {
            ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).setId(id);
            ((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).init();
            setProfile(((ViewCollectionBean) BeanHelper.getSessionBean(ViewCollectionBean.class)).getProfile());
            setCollection(((ViewCollectionBean)BeanHelper.getSessionBean(ViewCollectionBean.class)).getCollection());
            setSendEmailNotification(sessionBean.getUser().getObservedCollections().contains(id));
            LinkedList<Person> persons = new LinkedList<Person>();
            if (getCollection().getMetadata().getPersons().size() == 0)
            {
                getCollection().getMetadata().getPersons().add(new Person());
            }
            for (Person p : getCollection().getMetadata().getPersons())
            {
                LinkedList<Organization> orgs = new LinkedList<Organization>();
                for (Organization o : p.getOrganizations())
                {
                    orgs.add(o);
                }
                p.setOrganizations(orgs);
                persons.add(p);
            }
            getCollection().getMetadata().setPersons(persons);
            // set the loaded collection in the session
            ((CollectionSessionBean)BeanHelper.getSessionBean(CollectionSessionBean.class)).setActive(getCollection());

            //load Profiles When there is empty profile only 
            if ( getProfile().getStatements().size()==0) {
            	loadProfiles();
            	if (getProfileItems().size()==0) {
            		setUseMDProfileTemplate(false);
            	}
            	setUseMDProfileTemplate(true);
            }
            else
            {
            	setUseMDProfileTemplate(false);
            }
        }
        else
        {
            BeanHelper.error(sessionBean.getLabel("error") + " : no ID in URL");
        }
        
        if(UrlHelper.getParameterBoolean("start")){
    		upload();			
    	}
    	
    }

    public String save() throws Exception
    {
        if (valid())
        {
            
        	if (saveEditedCollection()) {
	            Navigation navigation = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
	            FacesContext
	                    .getCurrentInstance()
	                    .getExternalContext()
	                    .redirect(
	                            navigation.getCollectionUrl() + ObjectHelper.getId(getCollection().getId()) + "/"
	                                    + navigation.getInfosPath() + "?init=1");
        	}
        }
        return "";
    }

    public boolean saveEditedCollection()  {
    	 try {
		    	 CollectionController collectionController = new CollectionController();
		         User user = sessionBean.getUser();
		         //collectionController.updateLazy(getCollection(), user);
		         
		         
		         CollectionImeji icPre = collectionController.retrieve(getCollection().getId(), user);
		         CollectionImeji ic;
		        		 
		         if (isUseMDProfileTemplate() && getProfileTemplate() != null) {
		         	ic = collectionController.updateLazyWithProfile(getCollection(), getProfileTemplate(), user, 
		         							collectionController.getProfileCreationMethod(getSelectedCreationMethod()));
		         }
		         else
		         {
		            ic= collectionController.update(getCollection(), user);
		         }
		
		         if (icPre.getLogoUrl() != null && getCollection().getLogoUrl() == null) {
		        	 collectionController.updateCollectionLogo(icPre, null, user);
		         }
		         //System.out.println("In save edited Collection "+ic.getLogoUrl().toString());
		         UserController uc = new UserController(user);
		         uc.update(user, user);
		         
		         //here separate update for the Logo only, as it will only be allowed by edited collection through the web application
		         //not yet for REST 
		         //getIngestImage is inherited from Container!

		         if (getIngestImage()!= null ) {
		        	 collectionController.updateCollectionLogo(ic, getIngestImage().getFile(), user);
		        	 setIngestImage(null);
    	 		 }
		         

	        	 BeanHelper.info(sessionBean.getMessage("success_collection_save"));
		         return true;
    	 }
    	 catch (ImejiException e)
    	 {
    		 
    		 BeanHelper.info(sessionBean.getMessage("error_collection_save"));
    		 return false;
    	 } catch (IOException e) {
			// TODO Auto-generated catch block
    		 BeanHelper.info(sessionBean.getMessage("error_collection_logo_save"));
    		 return false;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			BeanHelper.info(sessionBean.getMessage("error_collection_logo_uri_save"));
   		 	return false;
		}

    }
    /**
     * Return the link for the Cancel button
     * 
     * @return
     */
    public String getCancel()
    {
        Navigation nav = (Navigation)BeanHelper.getApplicationBean(Navigation.class);
        return nav.getCollectionUrl() + ObjectHelper.getId(getCollection().getId()) + "/" + nav.getInfosPath()
                + "?init=1";
    }

    @Override
    protected String getNavigationString()
    {
        return "pretty:editCollection";
    }
    
	 /**
     * Method called on the html page to trigger the initialization of the bean
     * 
     * @return
     */
    public String getProfileSelect()
    {
       if (UrlHelper.getParameterBoolean("profileSelect"))
        {
        	setProfileSelectMode(true);
        }
        else
        {
        	setProfileSelectMode(false);
        }
        return "";
    }
    
	/**
	 * Method for save&editProfile button. Create the {@link MetadataProfile} according to
	 * the form
	 * 
	 * @return
	 * @throws Exception
	 */  
	public String saveAndEditProfile() throws Exception {
		if (saveEditedCollection()) {
			FacesContext.getCurrentInstance().getExternalContext().
			redirect(navigation.getProfileUrl()+getProfileId()+"/edit?init=1&col="+ getCollection().getIdString());
		}
		return "";
	}

}
