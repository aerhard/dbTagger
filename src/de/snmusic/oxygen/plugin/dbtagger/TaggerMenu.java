package de.snmusic.oxygen.plugin.dbtagger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import de.snmusic.oxygen.plugin.dbtagger.prefs.PrefsData;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

@SuppressWarnings("serial")
public class TaggerMenu extends Menu {

	StandalonePluginWorkspace workspace;
	
	TaggerMenu(StandalonePluginWorkspace workspace) {
		super("dbTagger", true);
		this.workspace = workspace;
	}

	/**
	 * Creates new instances of the menu items; called every time the user
	 * submits the preferences dialog with "OK".
	 */
	public void setMenuItems(PrefsData prefsData, Action openPrefs) {

		if (getItemCount() > 0) {
			removeAll();
		}

		// Add search buttons
		String[][] prefsSets = prefsData.getCurrentPreferences();
		for (final String[] prefsSet : prefsSets) {
			Action searchAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					Tagger t = new Tagger(workspace, prefsSet);
					t.distributeProcessing();
				}
			};
			JMenuItem searchButton = new JMenuItem();
			searchButton.setAction(searchAction);
			searchButton.setAccelerator(KeyStroke
					.getKeyStroke(prefsSet[PrefsData.SHORTCUT]));
			searchButton.setText(prefsSet[PrefsData.NAME]);
			add(searchButton);
		}
		
		if (prefsSets.length > 0) {
			addSeparator();
		}

		// Add preferences button
		JMenuItem prefsButton = new JMenuItem();
		prefsButton.setAction(openPrefs);
		prefsButton.setText("Einstellungen");
		add(prefsButton);
	};

}
