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

import java.awt.Component;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;

public class Tagger {

	protected StandalonePluginWorkspace workspace;
	private String[] prefsSet;

	public Tagger(StandalonePluginWorkspace workspace, String[] prefsSet) {
		this.workspace = workspace;
		this.prefsSet = prefsSet;
	}

	/**
	 * Checks the type of the currently active editor window. Breaks off if there is
	 * none, otherwise calls either the Text Editor or the Author Editor method
	 * for further processing.
	 */
	public void dispatchProcessing() {

		WSEditorPage currentPage = workspace.getCurrentEditorAccess(
				StandalonePluginWorkspace.MAIN_EDITING_AREA).getCurrentPage();

		if (currentPage != null) {
			if (currentPage instanceof WSTextEditorPage) {
				WSTextEditorPage editorPage = (WSTextEditorPage) currentPage;
				tagTextEditorPage(editorPage);
			} else {
				if (currentPage instanceof WSAuthorEditorPage) {
					WSAuthorEditorPage editorPage = (WSAuthorEditorPage) currentPage;
					tagAuthorEditorPage(editorPage);
				}
			}
		}
	};

	private void tagTextEditorPage(WSTextEditorPage editorPage) {

		String selection = (editorPage.hasSelection()) ? editorPage
				.getSelectedText() : "";

		String resultVals[] = null;
		DataChooser dialog = null;
		
		/*
		 * Open tagger dialog
		 */
		try {
			dialog = new DataChooser(workspace, selection, this.prefsSet);
			dialog.setLocationRelativeTo((Component) workspace.getParentFrame());
			resultVals = dialog.showDialog();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Insert results into editor pane
		 */
		if (resultVals != null) {
			/*
			 * Apply template
			 */
			// Gets the user defined template for this action
			String template = this.prefsSet[PrefsStore.TEXT_TPL];

			// Replace template variable "${key}" with result key
			String result = template.replace("${key}", resultVals[0]); // key
			// Replace template variable "${text}" with result text
			result = result.replace("${text}", resultVals[1]); // text
			// Replace template variable "${selection}" with document selection
			result = result.replace("${selection}", selection);

			// insert
			editorPage.beginCompoundUndoableEdit();
			int selectionOffset = editorPage.getSelectionStart();
			editorPage.deleteSelection();
			try {
				editorPage.getDocument().insertString(selectionOffset, result,
						javax.swing.text.SimpleAttributeSet.EMPTY);
			} catch (javax.swing.text.BadLocationException b) {
			}
			editorPage.endCompoundUndoableEdit();
		}
	}

	private void tagAuthorEditorPage(WSAuthorEditorPage editorPage) {
		AuthorAccess authorAccess = editorPage.getAuthorAccess();

		String selection = (editorPage.hasSelection()) ? editorPage
				.getSelectedText() : "";

		String resultVals[] = null;
		DataChooser dialog = null;
		
		/*
		 * Open tagger dialog
		 */
		try {
			dialog = new DataChooser(workspace, selection, this.prefsSet);
			dialog.setLocationRelativeTo((Component) workspace.getParentFrame());
			resultVals = dialog.showDialog();
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * Insert results into editor pane
		 */
		if (resultVals != null) {
			/*
			 * Apply template
			 */

			/*
			 * Gets the user defined template for this action; if no author mode
			 * template is specified by the user, use the text mode template
			 * instead
			 */
			String template = (this.prefsSet[PrefsStore.AUTHOR_TPL] == "") ? this.prefsSet[PrefsStore.TEXT_TPL]
					: this.prefsSet[PrefsStore.AUTHOR_TPL];

			// Replace template variable "${key}" with result key
			String result = template.replace("${key}", resultVals[0]);
			// Remove "{$selection}" from result
			result = result.replace("${selection}", "");

			if (result.contains("${text}")) {
				/*
				 * delete the selected document fragment; replace template
				 * variable "${text}" with result text; perform
				 * surroundWithFragment operation
				 */

				result = result.replace("${text}", resultVals[1]);
				AuthorDocumentController controller = authorAccess
						.getDocumentController();
				controller.beginCompoundEdit();
				boolean deleteSelection = false;
				try {
					if (authorAccess.getEditorAccess().hasSelection()) {
						deleteSelection = true;
						authorAccess.getEditorAccess().deleteSelection();
					}
					CommonsOperationsUtil.surroundWithFragment(authorAccess,
							false, result);
				} catch (AuthorOperationException e) {
					if (deleteSelection) {
						controller.cancelCompoundEdit();
					}
				} finally {
					controller.endCompoundEdit();
				}
			} else {
				/*
				 * surround the selected document fragment with the result
				 * string
				 */
				try {
					CommonsOperationsUtil.surroundWithFragment(authorAccess,
							false, result);
				} catch (AuthorOperationException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
