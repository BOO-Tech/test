importPackage(java.io);

function startExport(doc){ 
  try{
	  var v_arr3:java.util.Vector = new java.util.Vector();
       v_arr3 = viewScope.get("myList");
       v_arr3.clear();
       var view_list_nav:java.util.Vector = new java.util.Vector();
	  var server_name = doc.getItemValueString("server_name");
	  var db_name = doc.getItemValueString("db_name");
	  var all_views:java.util.Vector = doc.getItemValue("views");
	  var export_path = doc.getItemValueString("export_path");
	  var db_folder_split= db_name.split("/");
	  var db_folder = db_folder_split[db_folder_split.length-1];
	  db_folder = db_folder.toString().replace(".nsf","")
	  export_path = export_path+"/HTMLExport//"+db_folder
      view_list_nav.addAll(all_views);9

  	var url = "template.html";
	var left_nav = "";
    var data = facesContext.getExternalContext().
         getResourceAsStream( url );
     var raw_template:String = new String();
     while( data.available() ){
    	 raw_template += @Char(data.read());
     }
     
    
   try{
	
   
  	var javaCall = new com.dot.ExportHtml();
    var db:database = session.getDatabase(server_name,db_name);
  	raw_template = raw_template.replace("{{DATABASE__TITLE}}",db.getTitle()); 
  	raw_template = raw_template.replace("{{DATABASE__PATH}}",db_name); 


    
    
var progress_percent:float = 100/parseInt(all_views.size());
		var percent = 0;
  	for(var i=0;i < all_views.size();i++){
  		 var template = "";
  	   for(var a=0;a < view_list_nav.size();a++){
  		  
  		   template = raw_template;
	       if(view_list_nav.get(a).toString().equals(all_views.get(i).toString())){
	    	  left_nav += "<a href="+view_list_nav.get(a).toString().replace(" ", "")+".html class='w3-bar-item w3-button w3-padding w3-teal'><i class='fa fa-file fa-fw'></i> "+view_list_nav.get(a).toString()+"</a>"
	     }else{
	    	 left_nav += "<a href="+view_list_nav.get(a).toString().replace(" ", "")+".html class='w3-bar-item w3-button w3-padding'><i class='fa fa-file fa-fw'></i> "+view_list_nav.get(a).toString()+"</a>"

	     }
  	   }
  	 template = template.replace("{{LEFT__NAV}}",left_nav);  
  	left_nav = "";
		percent = percent + progress_percent;
            //view.postScript("console.log('"+all_views.get(i).toString()+"')")
  		var v:NotesView = db.getView(all_views.get(i).toString());

  
	var htmlView = "";
  	     htmlView = javaCall.createHtmlView(v,session,template,export_path,db);
  	if(htmlView.toString() == "Export__SUCCESS"){
  		getComponent("label1").setValue(getComponent("label1").getValue()+" Done");
  		view.postScript("updateExportedNumbers('"+all_views.size()+","+i+"')");
		view.postScript("updateExportedviewName('"+all_views.get(i).toString()+"')");
		view.postScript("updateprogressBar('"+percent+"')");
		v_arr3.add(v.getName()+"|Success")
  		
  	}else{
  	//	getComponent("label1").setValue("Error in JavaClass ExportHtml : "+htmlView.toString());
  		view.postScript("updateExportedNumbers('"+all_views.size()+","+i+"')");
		view.postScript("updateExportedviewName('"+all_views.get(i).toString()+"')");
		view.postScript("updateprogressBar('"+percent+"')");
		v_arr3.add(v.getName()+"|Error from JC ExportHtml : "+htmlView.toString());


  	}
  	template="";
  	}


  }catch(err){
	   
	getComponent("error_div").setRendered(true);
    getComponent("error").setValue(err+" => Catch 1");
    getComponent("progress").setRendered(false);
    getComponent("report").setRendered(false);
    doc.remove(true)
  }

  }catch(err){
		
	    getComponent("error_div").setRendered(true);
	    getComponent("error").setValue(err+" => Catch 0")
	    getComponent("progress").setRendered(false);
	    getComponent("report").setRendered(false);
	    doc.remove(true)

  
  }
}
  function writeToFile(){
  }