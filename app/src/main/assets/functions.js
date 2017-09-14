/**
 * Function to switch visible tab
 */
function openTab(tabName, btnName) {
   var i;
   var t = document.getElementsByClassName("tab");
   var b = document.getElementsByClassName("btnT");

   for (i = 0; i < t.length; i++) {
       t[i].style.display = "none";
   }
   for (i = 0; i < b.length; i++) {
       b[i].style = "border: 1px solid";
   }
   document.getElementById(tabName).style.display = "block";
   document.getElementById(btnName).style.border="2px solid";
   document.getElementById(btnName).style.outline="0";
}

/**
 * Replace content of fields according to jcontent JSON object
 * and set fields visibility acording to visibility JSON object (inside jcontent)
 */
function jreplace(jcontent) {
   jobj=JSON.parse(jcontent);

   for (var key in jobj) {
       if (document.getElementById(key))
           document.getElementById(key).innerHTML = jobj[key];
       else if (!key.localeCompare("visibility"))
           for (var v in jobj.visibility) {
               var elt = document.getElementById(v);
               if (elt) {
                   if (elt.nodeName.toLowerCase() == 'div')
                       elt.style.display = "block";
                   else
                       elt.style.display = "inline";
               } else
                   console.log("visibility ID "+v+" doesn't exists");
           }
       else
           console.log("ID "+key+" doesn't exists");
   }
}

/**
 * Callback function to start preferences activity
 */
function aCallback () {
    Android.launchPreferences();
}

/**
 * Callback function to show help
 */
function hCallback () {
    Android.launchHelp();
}

/**
 * Callback to launch File Manager
 */
function fCallback () {
    Android.launchFileManager();
}

/**
 * Callback function to send dump by E-Mail
 */
function eCallback () {
    Android.sendDump(
            getFileName(),
            document.getElementById("t_parser_error").textContent
    );
}

/**
 * Callback function to append remark to dump
 */
function rCallback () {
    var remark=prompt("Add your remark to dump","");
    if (remark != null && remark.length !=0) {
        remark = "REM: " + getFormattedDate() + "\n" + remark + "\n";
        Android.appendRemark(
                getFileName(),
                remark
        );
        var content = document.createTextNode(remark);
        document.getElementById("t_note_text").appendChild(content);
        document.getElementById("vt_note").style.display = "block";
    }
}

/**
 * Function to get actual file name
 */
function getFileName () {
    var fn = document.getElementById("t_real_file_name").textContent;
    if (fn != null && fn.length != 0)
        return fn
    return document.getElementById("t_file_name").textContent;
}

/**
 * Internal function to get date in format YYYY-MM-DD hh:mm:ss
 */
function getFormattedDate () {
    // from https://stackoverflow.com/a/30948017
    var d = new Date();

    d = d.getFullYear() 
		+ "-"
		+ ('0' + (d.getMonth() + 1)).slice(-2)
		+ "-"
		+ ('0' + d.getDate()).slice(-2)
		+ " "
		+ ('0' + d.getHours()).slice(-2)
		+ ":"
		+ ('0' + d.getMinutes()).slice(-2)
		+ ":"
		+ ('0' + d.getSeconds()).slice(-2)
	;

    return d;
}
