/**
 * Copyright 2013 Alexander Erhard
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.snmusic.oxygen.plugin.dbtagger;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ui.Menu;

/**
 * Creates the tagger plugin's menu and event.
 */
public class TaggerPluginExtension implements WorkspaceAccessPluginExtension {

	public Menu taggerMenu;
	public PrefsStore prefsStore;
	public StandalonePluginWorkspace workspace;

	public TaggerPluginExtension getPluginExtension() {
		return this;
	}

	public Menu getTaggerMenu() {
		return taggerMenu;
	}

	public PrefsStore getPrefsStore() {
		return prefsStore;
	}

	public void applicationStarted(final StandalonePluginWorkspace workspace) {

		this.taggerMenu = new Menu("dbTagger", true);
		this.prefsStore = new PrefsStore(workspace);
		this.workspace = workspace;

		setMenuItems();

		workspace.addMenuBarCustomizer(new MenuBarCustomizer() {

			public void customizeMainMenu(JMenuBar mainMenu) {
				mainMenu.add(taggerMenu,
						Math.max(mainMenu.getMenuCount() - 2, -1));
			}
		});
	};

	@Override
	public boolean applicationClosing() {
		return true;
	};

	/**
	 * Creates new instances of the menu items; called every time the user
	 * submits the preferences dialog with "OK".
	 */
	public void setMenuItems() {

		if (taggerMenu.getItemCount() > 0)
			taggerMenu.removeAll();

		// Add search buttons
		String[][] prefsSets = prefsStore.getCurrentPrefsSets();
		for (final String[] prefsSet : prefsSets) {
			Action searchAction = new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					Tagger t = new Tagger(workspace, prefsSet);
					t.dispatchProcessing();
				}
			};
			JMenuItem searchButton = new JMenuItem();
			searchButton.setAction(searchAction);
			searchButton.setAccelerator(KeyStroke
					.getKeyStroke(prefsSet[PrefsStore.SHORTCUT]));
			searchButton.setText(prefsSet[PrefsStore.NAME]);
			taggerMenu.add(searchButton);
		}

		if (prefsSets.length > 0)
			taggerMenu.addSeparator();

		// Add preferences button
		JMenuItem prefsButton = new JMenuItem();
		Action openPrefs = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				PrefsEditor dialog = new PrefsEditor(workspace,
						getPluginExtension());
				dialog.show();
			}
		};
		prefsButton.setAction(openPrefs);
		prefsButton.setText("Einstellungen");
		taggerMenu.add(prefsButton);
	};

}