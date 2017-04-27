/*******************************************************************************
 * Copyright (c) 2009, 2017, The University of Manchester
 *
 * Licensed under the New BSD License.
 * Please see LICENSE file that is distributed with the source code
 *  
 *******************************************************************************/
package uk.ac.manchester.cs.owl.semspreadsheets.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;

import uk.ac.manchester.cs.owl.semspreadsheets.repository.RepositoryItem;

/**
 * A ListModel that provides the ability to filter over a list
 * of RepositoryItem's based upon the human readable name for the repository
 * item matching a given piece of text (case insensitive).
 * 
 * The filtering is initiated by calling {@link #filterBy(String)}
 * 
 * @author Stuart Owen 
 */
@SuppressWarnings("serial")
class FilteredRepositoryItemListModel extends AbstractListModel<RepositoryItem> {
	
	private final List<RepositoryItem> items;
	private final List<RepositoryItem> filteredItems;

	public FilteredRepositoryItemListModel(List<RepositoryItem> items) {
		this.items = items;    		
		filteredItems = new ArrayList<RepositoryItem>(items);			
	}

	@Override
	public RepositoryItem getElementAt(int index) {
		return filteredItems.get(index);
	}

	@Override
	public int getSize() {
		return filteredItems.size();
	}
	
	public void filterBy(String text) {
		filteredItems.clear();
		text=text.toLowerCase();
		for (RepositoryItem i : items) {
			if (i.getHumanReadableName().toLowerCase().contains(text)){
				filteredItems.add(i);
			}
		}
		fireContentsChanged(this, 0, getSize());		
	}
	
}
