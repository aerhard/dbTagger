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
import java.util.Arrays;

import org.apache.log4j.Logger;

import de.snmusic.oxygen.plugin.dbtagger.prefs.PrefsData;
import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.ecss.extensions.api.AuthorDocumentController;
import ro.sync.ecss.extensions.api.AuthorOperationException;
import ro.sync.ecss.extensions.commons.operations.CommonsOperationsUtil;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.editor.page.WSEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;

public class Tagger {

	public static final String LOGGER = "de.snmusic.oxygen.plugin.dbtagger";

	private Logger logger = Logger.getLogger(LOGGER);

	private StandalonePluginWorkspace workspace;
	private String[] prefsSet;

	public Tagger(StandalonePluginWorkspace workspace, String[] prefsSet) {
		this.workspace = workspace;
		this.prefsSet = Arrays.copyOf(prefsSet, prefsSet.length);
	}

	/**
	 * Checks the type of the currently active editor window. Breaks off if
	 * there is none, otherwise calls either the Text Editor or the Author
	 * Editor method for further processing.
	 */
	public void distributeProcessing() {

		WSEditorPage currentPage = (workspace).getCurrentEditorAccess(
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

		resultVals = openDialog(selection);

		if (resultVals != null) {
			String result = applyTemplate(this.prefsSet[PrefsData.TEXT_TPL],
					selection, resultVals);
			insertInEditorPage(editorPage, result);
		}
	}

	private String[] openDialog(String selection) {
		QueryWindow dialog;
		String[] resultVals = null;
		try {
			dialog = new QueryWindow(workspace, selection, this.prefsSet);
			dialog.setLocationRelativeTo((Component) workspace.getParentFrame());
			resultVals = dialog.showDialog();
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
		}
		return resultVals;
	}

	private String applyTemplate(String template, String selection,
			String[] resultVals) {
		String result = template.replaceAll("\\$\\{selection\\}", selection);
		for (int i = 0; i < resultVals.length; i++) {
			result = result.replaceAll("\\$\\{" + (i + 1) + "\\}",
					resultVals[i]);
		}
		return result;
	}

	private void insertInEditorPage(WSTextEditorPage editorPage, String result) {
		editorPage.beginCompoundUndoableEdit();
		int selectionOffset = editorPage.getSelectionStart();
		editorPage.deleteSelection();
		try {
			editorPage.getDocument().insertString(selectionOffset, result,
					javax.swing.text.SimpleAttributeSet.EMPTY);
		} catch (javax.swing.text.BadLocationException b) {
			if (logger.isDebugEnabled()) {
				logger.debug(b, b);
			}
		}
		editorPage.endCompoundUndoableEdit();
	}

	private void tagAuthorEditorPage(WSAuthorEditorPage editorPage) {
		AuthorAccess authorAccess = editorPage.getAuthorAccess();

		String selection = (editorPage.hasSelection()) ? editorPage
				.getSelectedText() : "";

		String resultVals[] = null;
		resultVals = openDialog(selection);

		/*
		 * Insert results into editor pane
		 */
		if (resultVals != null) {
			/*
			 * Gets the user defined template for this action; if no author mode
			 * template is specified by the user, use the text mode template
			 * instead
			 */
			String template = ("".equals(this.prefsSet[PrefsData.AUTHOR_TPL])) ? this.prefsSet[PrefsData.TEXT_TPL]
					: this.prefsSet[PrefsData.AUTHOR_TPL];

			String result = applyTemplate(template, "", resultVals);

			if (template.contains("${selection}")) {
				insertInAuthorPageSurround(authorAccess, result);
			} else {
				insertInAuthorPageReplace(authorAccess, result);
			}
		}
	}

	private void insertInAuthorPageReplace(AuthorAccess authorAccess,
			String result) {
		AuthorDocumentController controller = authorAccess
				.getDocumentController();
		controller.beginCompoundEdit();
		boolean deleteSelection = false;
		try {
			if (authorAccess.getEditorAccess().hasSelection()) {
				deleteSelection = true;
				authorAccess.getEditorAccess().deleteSelection();
			}
			CommonsOperationsUtil.surroundWithFragment(authorAccess, false,
					result);
		} catch (AuthorOperationException e) {
			if (deleteSelection) {
				controller.cancelCompoundEdit();
			}
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
		} finally {
			controller.endCompoundEdit();
		}
	}

	private void insertInAuthorPageSurround(AuthorAccess authorAccess,
			String result) {
		try {
			CommonsOperationsUtil.surroundWithFragment(authorAccess, false,
					result);
		} catch (AuthorOperationException e) {
			if (logger.isDebugEnabled()) {
				logger.debug(e, e);
			}
		}
	}
}
