/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.logic.export.format;

import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBException;

import de.mpg.imeji.logic.controller.ItemController;
import de.mpg.imeji.logic.export.Export;
import de.mpg.imeji.logic.ingest.jaxb.JaxbIngestProfile;
import de.mpg.imeji.logic.ingest.vo.Items;
import de.mpg.imeji.logic.search.SearchResult;
import de.mpg.imeji.logic.search.vo.SearchQuery;
import de.mpg.imeji.logic.search.vo.SortCriterion;
import de.mpg.imeji.logic.vo.CollectionImeji;
import de.mpg.imeji.logic.vo.Item;
import de.mpg.imeji.presentation.collection.ViewCollectionBean;
import de.mpg.imeji.presentation.session.SessionBean;
import de.mpg.imeji.presentation.util.BeanHelper;

/**
 * Export the information for the ingest issue
 * 
 * @author hnguyen
 */
public class IngestItemsExport extends Export
{
    @Override
    public void init()
    {
        // Not initialization so far
    }

    @Override
    public void export(OutputStream out, SearchResult sr)
    {
    	SessionBean session = (SessionBean)BeanHelper.getSessionBean(SessionBean.class);        
    	ItemController ic = new ItemController(session.getUser());
    	Collection<Item> itemList = ic.loadItems(sr.getResults(), -1,0);
        Items items = new Items(itemList);
        try {
			JaxbIngestProfile.writeToOutputStream(items,out);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   		
    }

    @Override
    public String getContentType()
    {
        return "application/xml";
    }
}