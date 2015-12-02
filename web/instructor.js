$(document).ready(function () {
    $("#create_exam_date_begin").datepicker();
    $("#create_exam_date_end").datepicker();

    $("#create_exam_date_begin, #create_exam_date_end, #create_exam_section").change(function () {
        if ($("#create_exam_date_begin_input").val().length > 0 &&
                $("#create_exam_date_end_input").val().length > 0 &&
                $("#create_exam_section_input").val().length > 0) {
            $("#create_exam_submit").prop("disabled", false);
        } else {
            $("#create_exam_submit").prop("disabled", true);
        }
    });

    var submitting = false;

    $("#create_exam_submit").click(function () {
        // SUBMIT DETAILS TO SERVER
        var date_begin = $("#create_exam_date_begin_input").val();
        var date_end = $("#create_exam_date_end_input").val();
        var section = $("#create_exam_section_input").val();
        var students = $("#create_exam_students_input").val();
        var duration = $("#create_exam_duration_input").val();
        var classID = $("#create_exam_class_ID_input").val();

        submitting = true;

        $("#create_exam_submit").prop("disabled", true);
        $("#create_exam_submit").prop("class", "btn btn-default btn-warning");
        $("#create_exam_submit").html("<span id='create_exam_loading_icon' class='glyphicon glyphicon-refresh glyphicon-refresh-animate'></span> Submitting...");
        createExam()
        function createExam() {
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function () {
                if (xhttp.readyState == 4 && xhttp.status == 200) {
                    
                }
            };
            var url = "createExam?" + 'dateBegin=' + date_begin +
                    '&dateEnd=' + date_end + '&section=' + section +
                    '&students=' + students + '&duration=' + duration
                    + '&classID=' + classID + '&Id=' + sessionStorage['ID'];
            xhttp.open("POST", url, true);
            xhttp.send(url);
        }
    });

    $("#create_exam_close").click(function () {
        if (submitting) {
            $("#create_exam_submit").prop("class", "btn btn-default");
            $("#create_exam_submit").html("<span id='create_exam_loading_icon' class='glyphicon glyphicon-refresh'></span> Submit");

            submitting = false;
        }

        $("#create_exam_submit").prop("disabled", true);
        $("#create_exam_date_begin_input").val("");
        $("#create_exam_date_end_input").val("");
        $("#create_exam_section_input").val("");
    });

    $("#calendar")[0].addEventListener("day_click", function (e) {
        var element = e.element;

        if (element.hasEvent) {
            console.log("HAS_EVENT");
        } else {
            console.log("NO_EVENT");
        }
    });
});
