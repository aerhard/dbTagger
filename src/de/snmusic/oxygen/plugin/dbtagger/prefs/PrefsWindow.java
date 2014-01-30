/*
 * Copyright 2013 Alexander Erhard

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package de.snmusic.oxygen.plugin.dbtagger.prefs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import de.snmusic.oxygen.plugin.dbtagger.TaggerPluginExtension;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Creates the user preferences editor dialog.
 */
public class PrefsWindow {

	private PrefsTable table;
	private TaggerPluginExtension pluginExtension;
	private StandalonePluginWorkspace workspace;
	
	public PrefsWindow(StandalonePluginWorkspace workspace,
			TaggerPluginExtension pluginExtension) {

		this.pluginExtension = pluginExtension;
		this.workspace = workspace;
		
		this.table = new PrefsTable(pluginExtension.getPreferences());

	};

	public void show() {
		String windowTitle = "dbTagger-Einstellungen";

		JPanel prefsPane = new JPanel();
		prefsPane.setLayout(new BoxLayout(prefsPane, BoxLayout.PAGE_AXIS));

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(800, 400));
		prefsPane.add(scrollPane);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.setBorder(BorderFactory.createLoweredBevelBorder());
		addButtonsTo(buttonPane);
		prefsPane.add(buttonPane);

		table.init();

		int result = JOptionPane.showConfirmDialog(
				(java.awt.Frame) workspace.getParentFrame(), prefsPane,
				windowTitle, JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			pluginExtension.getPreferences().setCurrentPreferences(
					table.getTableData());
			pluginExtension.initMenu();
		}
	}

	private void addButtonsTo(JPanel buttonPane) {
		JButton newButton = new JButton("Neu");
		JButton deleteButton = new JButton("Löschen");
		JButton duplicateButton = new JButton("Duplizieren");
		JButton resetButton = new JButton("Zurücksetzen");

		Dimension d = resetButton.getMaximumSize();
		newButton.setMaximumSize(new Dimension(d));
		deleteButton.setMaximumSize(new Dimension(d));
		duplicateButton.setMaximumSize(new Dimension(d));

		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.addRow();
			}
		});
		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.deleteRow();
			}
		});
		duplicateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.duplicateRow();
			}
		});
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				table.init();
			}
		});
		buttonPane.add(newButton);
		buttonPane.add(deleteButton);
		buttonPane.add(duplicateButton);
		buttonPane.add(resetButton);
	};

}
