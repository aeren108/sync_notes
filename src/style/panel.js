document.getElementById("bold").onclick = function() {document.execCommand("bold")}
document.getElementById("underline").onclick = function() {document.execCommand("underline")}
document.getElementById("italic").onclick = function() {document.execCommand("italic")}
document.getElementById("strike").onclick = function() {document.execCommand("strikethrough")}
document.getElementById("ulist").onclick = function() {document.execCommand("insertUnorderedList")}

document.getElementById("editor").focus();