package de.snmusic.oxygen.plugin.dbtagger.data;

import java.util.Arrays;

public class TableData {

	private String[] columnTitles;
	private String[][] data;

	TableData(String[] columnTitles, String[][] data) {
		this.columnTitles = Arrays.copyOf(columnTitles, columnTitles.length);
		this.data = Arrays.copyOf(data, data.length);
	}

	public String[] getColumnTitles() {
		return columnTitles;
	}

	public String[][] getData() {
		return data;
	}

}
