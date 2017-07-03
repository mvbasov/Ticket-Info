
function openTab(tabName, btnName) {
   var i;
   var t = document.getElementsByClassName("tab");
   var b = document.getElementsByClassName("btn");

   for (i = 0; i < t.length; i++) {
       t[i].style.display = "none";
   }
   for (i = 0; i < b.length; i++) {
       b[i].style = "border: 1px solid";
   }
 //document.getElementById(tabName).style.display = "inline-block";
   document.getElementById(tabName).style.display = "block";
   document.getElementById(btnName).style.border="2px solid";
   document.getElementById(btnName).style.outline="0";
}

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

function aCallback () {
    Android.launchPreferences();
}

function eCallback () {
    Android.sendDump(document.getElementById("t_file_name").textContent);
}
