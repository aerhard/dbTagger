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

import de.snmusic.oxygen.plugin.dbtagger.prefs.PrefsData;
import de.snmusic.oxygen.plugin.dbtagger.prefs.PrefsWindow;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Creates the tagger plugin's menu and event.
 */
public class TaggerPluginExtension implements WorkspaceAccessPluginExtension {

	private TaggerMenu taggerMenu;
	private PrefsData prefsData;
	private StandalonePluginWorkspace workspace;

	public TaggerPluginExtension getPluginExtension() {
		return this;
	}

	public TaggerMenu getTaggerMenu() {
		return taggerMenu;
	}

	public PrefsData getPreferences() {
		return prefsData;
	}

	public void applicationStarted(final StandalonePluginWorkspace workspace) {

		this.workspace = workspace;

		this.taggerMenu = new TaggerMenu(workspace);
		this.prefsData = new PrefsData(workspace);

		initMenu();

		workspace.addMenuBarCustomizer(new MenuBarCustomizer() {
			public void customizeMainMenu(JMenuBar mainMenu) {
				mainMenu.add(taggerMenu,
						Math.max(mainMenu.getMenuCount() - 2, -1));
			}
		});
	}

	public void initMenu() {
		taggerMenu.setMenuItems(this.prefsData, getOpenPrefsAction(workspace));
	}

	private Action getOpenPrefsAction(final StandalonePluginWorkspace workspace) {
		Action openPrefs = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				PrefsWindow dialog = new PrefsWindow(workspace,
						getPluginExtension());
				dialog.show();
			}
		};
		return openPrefs;
	};

	@Override
	public boolean applicationClosing() {
		return true;
	};

}
