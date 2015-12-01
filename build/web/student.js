$(document).ready(function(){
    $("#calendar")[0].addEventListener("day_click", function(e){
        var element = e.element;
        
        if (element.hasEvent) {
            // CLEAR MODAL BODY OF PREVIOUS CONTENT
            var view_exam_body = $("#view_exam_body");
            
            view_exam_body[0].innerHTML = "";
            
            // INITIALIZE MODAL BODY WITH EXAM INFO
            
            // SHOW MODAL
            $("#view_exam").modal("show");
        }
    });
    
    $("#notification_bar")[0].innerHTML += "<table class='event table notify'><tr><th>Exam</th></tr><tr><th>Class</th></tr></table>";
    $("#notification_bar")[0].innerHTML += "<table class='event table notify'><tr><th>Exam</th></tr><tr><th>Class</th></tr></table>";
});