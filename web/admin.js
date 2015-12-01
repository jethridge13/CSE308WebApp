function respondToCalendar() {
    console.log("HERE");
}

$(document).ready(function(){
    $("#calendar")[0].addEventListener("initialized", respondToCalendar);
});