

visibleForm = null;

function showCreationForm(name, type) {
    hideCreationForm();
    visibleForm = "create_"+name+"_"+type;
    document.getElementById(visibleForm).style.display="block";
}

function hideCreationForm() {
    if(visibleForm != null) { // hide currently active form
        document.getElementById(visibleForm).style.display="none";
    }
    visibleForm = null;
}
