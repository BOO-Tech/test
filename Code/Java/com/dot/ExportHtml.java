package com.dot;


import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import lotus.domino.*;


public class ExportHtml{
    @SuppressWarnings("unchecked")
	public StringBuilder createHtmlView(View v,Session session,String template,String export_path,Database db) throws NotesException {
		StringBuilder htmlStringBuilder = new StringBuilder();
		StringBuilder statusBuilder = new StringBuilder();
      try {
		
			ViewEntryCollection vEntryColl = v.getAllEntries();
			ViewEntry firstEntry = vEntryColl.getFirstEntry();
			ViewEntry tempEntry = null;
			Vector viewColumns = v.getColumns();
			Vector columnCatergoryAppliedValue = new Vector();
			Vector notesViewdata = new Vector();
			String categoryName = "";
			String docId = "";
			String default_path = "file:///D:/SampleDB/";
		
			// append table section
			htmlStringBuilder.append("<table class='w3-table w3-bordered w3-hoverable w3-white w3-small animated fadeIn'>");
			// append row
			htmlStringBuilder.append("<tr class='w3-grey w3-round'>");
			int columnCount = 0;
			for (int i = 0; i < viewColumns.size(); i++) {
				ViewColumn column1 = (ViewColumn) viewColumns.get(i);
				if (!column1.isHidden() && !column1.getFormula().contains("Previous version as of")) {
					columnCount++;
					htmlStringBuilder.append("<th>" + viewColumns.get(i) + "</th>");
				}
			}
			htmlStringBuilder.append("</tr>");
			int NotCategorizedParentTR = 0;
			while (firstEntry != null) {
				notesViewdata.clear();
				int isTRCreat = 0;
				String isCetagory = "";
				tempEntry = vEntryColl.getNextEntry();
				Document doc = firstEntry.getDocument();
				String archivedInfo = doc.getItemValueString("$DAArcIds");
				String[] arrOfStr = archivedInfo.split("!");

				String path = null;
				for (String a : arrOfStr) {
					if (a.indexOf("html") != -1) {
						path = a;
						path = path.replace("html", "");
						path = path.replaceAll("..", "$0/");
						path = path.substring(0, path.length() - 1) + ".pdf";
						path = default_path + "html/" + path;
					}
				}

				int c = 0;
				for (int i = 0; i < viewColumns.size(); i++) {

					ViewColumn column = (ViewColumn) viewColumns.get(i);
					if (!column.isHidden()) {
						if (column.isField()) {
						
							Vector fieldColumnValue = doc.getItemValue(column.getItemName());
							if(fieldColumnValue.isEmpty()){
								fieldColumnValue.add("");
							}
							Vector duplicateColumnEval = new java.util.Vector();
							if (column.isCategory() && fieldColumnValue.get(0).toString().isEmpty()) {
								fieldColumnValue.add("NotCategorized");

							} else {
								if (column.isCategory()) {
									if (!categoryName.isEmpty()) {
										if (fieldColumnValue.contains(categoryName)
												&& !doc.getUniversalID().toString().equals(docId)) {
											fieldColumnValue.clear();
											fieldColumnValue.add(categoryName);
										} else {
											if (doc.getUniversalID().toString().equals(docId)) {
												columnCatergoryAppliedValue.add(categoryName);
											}
											Vector indexE = new java.util.Vector();
											Collections.sort(fieldColumnValue, new Comparator<String>() {
												public int compare(String o1, String o2) {
													return o1.compareToIgnoreCase(o2);
												}
											});
											duplicateColumnEval.addAll(fieldColumnValue);
											for (int j = 0; j < duplicateColumnEval.size(); j++) {
												if (columnCatergoryAppliedValue.contains(duplicateColumnEval.get(j))) {

													fieldColumnValue.remove(duplicateColumnEval.get(j));

												}
											}

											columnCatergoryAppliedValue.add(categoryName);

										}
									} else {
										Collections.sort(fieldColumnValue, new Comparator<String>() {
											public int compare(String o1, String o2) {
												return o1.compareToIgnoreCase(o2);
											}
										});

									}
								}

							}

							if (column.isCategory() && !fieldColumnValue.get(0).toString().equals(categoryName)) {
								 System.out.println(fieldColumnValue.get(0).toString());

								categoryName = (String) fieldColumnValue.get(0).toString();
								 System.out.println(fieldColumnValue.get(0).toString());

								htmlStringBuilder.append("<tr id='parentF' class='w3-hover'>");
								htmlStringBuilder.append("<td colspan='" + columnCount + "'><span id=\"expand_"
										+ categoryName.replace(" ", "") + "\"  onclick=\"$('."
										+ categoryName.replace(" ", "") + "').show();$('#expand_"
										+ categoryName.replace(" ", "") + "').hide();$('#colapse_"
										+ categoryName.replace(" ", "")
										+ "').show();event.stopImmediatePropagation();\" style=\"display:none\" class='w3-text-teal fa fa-plus-square w3-xlarge'>&nbsp </span><span id=\"colapse_"
										+ categoryName.replace(" ", "") + "\"  onclick=\"event.preventDefault();$('."
										+ categoryName.replace(" ", "") + "').hide();$('#expand_"
										+ categoryName.replace(" ", "") + "').show();$('#colapse_"
										+ categoryName.replace(" ", "")
										+ "').hide();event.stopImmediatePropagation();\" class='w3-text-teal fa fa-minus-square w3-xlarge'>&nbsp</span>"
										+ fieldColumnValue.get(0) + "</td>");
								htmlStringBuilder.append("</tr>");
							} else if (column.isCategory() && fieldColumnValue.get(0).toString().equals(categoryName)) {
								if (isTRCreat == 0) {
									htmlStringBuilder
											.append("<tr id='ChildF' class='w3-hover' style='cursor:pointer' onclick=\"window.open('"
													+ path + "')\">");
									isTRCreat = 1;
								}
								htmlStringBuilder
										.append("<td class='" + categoryName.replace(" ", "") + "'>&nbsp&nbsp</td>");
							} else {
								if (v.isCategorized()) {
									if (isTRCreat == 0) {
										htmlStringBuilder
												.append("<tr id='ChildF' class='w3-hover' style='cursor:pointer' onclick=\"window.open('"
														+ path + "')\">");
										htmlStringBuilder.append(
												"<td class='" + categoryName.replace(" ", "") + "'>&nbsp&nbsp</td>");
										isTRCreat = 1;
									}
									htmlStringBuilder.append("<td class='" + categoryName.replace(" ", "") + "'>"
											+ fieldColumnValue.get(0) + "</td>");
									notesViewdata.add(fieldColumnValue.get(0));
								} else {
									if (isTRCreat == 0) {
										htmlStringBuilder
												.append("<tr id='ChildF' class='w3-hover' style='cursor:pointer' onclick=\"window.open('"
														+ path + "')\">");
										isTRCreat = 1;
									}
									htmlStringBuilder.append("<td class='" + categoryName.replace(" ", "") + "'>"
											+ fieldColumnValue.get(0) + "</td>");
									notesViewdata.add(fieldColumnValue.get(0));
								}
							}
						} else {
							if (column.isFormula() && !column.getFormula().contains("Previous version as of")) {
								Vector formulaColumnValue = new java.util.Vector();
								if(column.getFormula().equals("@WhichFolders")){
									String FolderNames = getFolderNames(db,doc.getUniversalID());
									
									formulaColumnValue.add(FolderNames);
									
									
								}else{
									 formulaColumnValue = session.evaluate(column.getFormula(), doc);
								}
								
								Vector duplicateColumnEval = new java.util.Vector();
								if (column.isCategory() && formulaColumnValue.get(0).toString().equals("")) {
									formulaColumnValue.clear();
									formulaColumnValue.add("NotCategorized");

								}  else {
									if (column.isCategory()) {

										if (!categoryName.isEmpty()) {
											if (formulaColumnValue.get(0).toString().equals("MS Office"))
												System.out.println(doc.getUniversalID().toString().equals(docId));
											if (formulaColumnValue.contains(categoryName)
													&& !doc.getUniversalID().toString().equals(docId)) {
												formulaColumnValue.clear();
												formulaColumnValue.add(categoryName);
											} else {
												if (doc.getUniversalID().toString().equals(docId)) {
													columnCatergoryAppliedValue.add(categoryName);
												}
												Vector indexE = new java.util.Vector();
												Collections.sort(formulaColumnValue, new Comparator<String>() {
													public int compare(String o1, String o2) {
														return o1.compareToIgnoreCase(o2);
													}
												});
												duplicateColumnEval.addAll(formulaColumnValue);
												for (int j = 0; j < duplicateColumnEval.size(); j++) {
													if (columnCatergoryAppliedValue.contains(duplicateColumnEval.get(j))) {

														formulaColumnValue.remove(duplicateColumnEval.get(j));

													}
												}

												columnCatergoryAppliedValue.add(categoryName);

											}
										} else {
											Collections.sort(formulaColumnValue, new Comparator<String>() {
												public int compare(String o1, String o2) {
													return o1.compareToIgnoreCase(o2);
												}
											});

										}
									}

								}

								if (column.isCategory() && !formulaColumnValue.get(0).toString().equals(categoryName)) {
									categoryName = (String) formulaColumnValue.get(0).toString();
									htmlStringBuilder.append("<tr id='parent' class='w3-hover'>");
									htmlStringBuilder.append("<td colspan='" + columnCount + "'><span id=\"expand_"
											+ categoryName.replace(" ", "") + "\"  onclick=\"$('."
											+ categoryName.replace(" ", "") + "').show();$('#expand_"
											+ categoryName.replace(" ", "") + "').hide();$('#colapse_"
											+ categoryName.replace(" ", "")
											+ "').show();event.stopImmediatePropagation();\" style=\"display:none\" class='w3-text-teal fa fa-plus-square w3-xlarge'>&nbsp</span><span id=\"colapse_"
											+ categoryName.replace(" ", "")
											+ "\"  onclick=\"event.preventDefault();$('."
											+ categoryName.replace(" ", "") + "').hide();$('#expand_"
											+ categoryName.replace(" ", "") + "').show();$('#colapse_"
											+ categoryName.replace(" ", "")
											+ "').hide();event.stopImmediatePropagation();\" class='w3-text-teal fa fa-minus-square w3-xlarge'>&nbsp</span>"
											+ formulaColumnValue.get(0) + "</td>");
									htmlStringBuilder.append("</tr>");

								} else if (column.isCategory() && formulaColumnValue.get(0).toString().equals(categoryName)) {
									if (isTRCreat == 0) {
										htmlStringBuilder
												.append("<tr id='ChildF' class='w3-hover' style='cursor:pointer' onclick=\"window.open('"
														+ path + "')\">");
										isTRCreat = 1;
									}
									htmlStringBuilder.append(
											"<td class='" + categoryName.replace(" ", "") + "'>&nbsp&nbsp</td>");
								} else {
									if (v.isCategorized()) {
										if (isTRCreat == 0) {
											htmlStringBuilder
													.append("<tr id='ChildF' class='w3-hover' style='cursor:pointer' onclick=\"window.open('"
															+ path + "')\">");
											htmlStringBuilder.append("<td class='" + categoryName.replace(" ", "")
													+ "'>&nbsp&nbsp</td>");

											isTRCreat = 1;
										}
										htmlStringBuilder.append("<td class='" + categoryName.replace(" ", "") + "'>"
												+ formulaColumnValue.get(0) + "</td>");
										notesViewdata.add(formulaColumnValue.get(0));
									} else {
										if (isTRCreat == 0) {
											htmlStringBuilder
													.append("<tr id='ChildF' class='w3-hover' style='cursor:pointer' onclick=\"window.open('"
															+ path + "')\">");
											isTRCreat = 1;
										}
										htmlStringBuilder.append("<td class='" + categoryName.replace(" ", "") + "'>"
												+ formulaColumnValue.get(0) + "</td>");
										notesViewdata.add(formulaColumnValue.get(0));
									}
								}
							}
						}

					}

					if (viewColumns.size() == i + 1) {
						htmlStringBuilder.append("</tr>");
					}

				}

				firstEntry.recycle();
				firstEntry = tempEntry;
				docId = doc.getUniversalID();

			}

			// close html file
			htmlStringBuilder.append("</table>");
			template = template.replace("{{VIEW__TABLE}}", htmlStringBuilder);
			template = template.replace("{{VIEW__NAME}}", v.getName());

			statusBuilder.append(template);
			
			try{
			//	String export_path = "D:views/htmlEX/abc/";
				String filePath = export_path + File.separator + v.getName().toString().replace(" ","") + ".html";
				//	System.out.println("test");
					
					    File directory = new File(export_path);
					//	System.out.println("test");

					    if (! directory.exists()){
					//		System.out.println("test");

					        directory.mkdirs();
					        
					        // If you require it to make the entire directory path including parents,
					        // use directory.mkdirs(); here instead.
					    }else{
							System.out.println("test");

					    	System.out.println(directory.exists());
					    }
					    
						File file = new File(filePath);
					
					// if file does exists, then delete and create a new file
					/*
					 * if (file.exists()) { try { File newFileName = new File(projectPath +
					 * File.separator+ "backup_"+fileName); file.renameTo(newFileName);
					 * file.createNewFile(); } catch (IOException e) { e.printStackTrace();
					 * } }
					 */
					// write to file with OutputStreamWriter
					

					
					OutputStream outputStream = new FileOutputStream(file.getAbsoluteFile());
					Writer writer = new OutputStreamWriter(outputStream);
					writer.write(statusBuilder.toString());
					writer.close();
					statusBuilder = new StringBuilder();
					return statusBuilder.append("Export__SUCCESS");
			}catch (Exception e) {
				statusBuilder = new StringBuilder();
				return statusBuilder.append(e.toString()+" => In I/O Catch");
			}
		

      } catch(Exception e) {
        //  e.printStackTrace();
  		 htmlStringBuilder = new StringBuilder();
   	//	htmlStringBuilder.append("Error0");
  		return htmlStringBuilder.append(v.getName()+"="+e.toString()+" => Main Catch");
       }
  	
	
   }

	private String getFolderNames(Database db, String universalID) {
		// TODO Auto-generated method stub
		String folderNames = "";

		try {
			Vector allViews =  db.getViews();
			for (int i = 0; i < allViews.size(); i++) {
               	View view = (View) allViews.get(i);
               	if(view.isFolder()){
               		ViewEntryCollection entry_coll  = view.getAllEntries();
               		ViewEntry entry = entry_coll.getFirstEntry();
               		while (entry != null) {
               			String docID = entry.getDocument().getUniversalID();
               			if(docID.equals(universalID)){
               				if(folderNames.isEmpty())
               					folderNames = view.getName();
               				else
               					folderNames +=","+view.getName();
               			}
               			
               			
               			ViewEntry tmpentry = entry_coll.getNextEntry(entry);
               			entry.recycle();
               			entry = tmpentry;
               			
                   		}
               	}               	
			}
			return folderNames;

		} catch (NotesException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return e.getLocalizedMessage();
		}
		
	}
}
