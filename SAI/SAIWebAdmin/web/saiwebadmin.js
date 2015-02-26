
function showCreationForm(name, type) {
    document.getElementById("select_"+name).style.display="none";
    document.getElementById("create_"+name+"_"+type).style.display="block";
}

function hideCreationForm(name, type) {
    document.getElementById("select_"+name).style.display="block";
    document.getElementById("create_"+name+"_"+type).style.display="none";
}
