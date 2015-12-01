$(document).ready(function() {
    var d = new Date();
    
    function initializeCalendar(d) {
        var today = new Date();
        
        var date = d.getDate();
        var day = d.getDay();
        var month = d.getMonth();

        var month_header = $("#month_header1")[0];

        var months = [
            "January", "February", "March", "April",
            "May", "June", "July", "August", "September",
            "October", "November", "December"
        ];

        var num_days = [
            31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
        ];

        var month_string = months[month];

        month_header.innerHTML = "";

        var leftButton = document.createElement("BUTTON");
        var rightButton = document.createElement("BUTTON");
        var textNode = document.createTextNode (
                month_string + " " + d.getFullYear()
        );

        month_header.appendChild(leftButton);
        month_header.appendChild(textNode);
        month_header.appendChild(rightButton);

        leftButton.className = "calendar_button_left";
        rightButton.className = "calendar_button_right";

        var leftSpan = document.createElement("SPAN");

        leftSpan.className = "glyphicon glyphicon-chevron-left";
        leftButton.appendChild(leftSpan);

        var rightSpan = document.createElement("SPAN");

        rightSpan.className = "glyphicon glyphicon-chevron-right";
        rightButton.appendChild(rightSpan);

        var day_boxes = [];

        for (var i = 0; i < 25; i++) {
            day_boxes.push($("#day_box" + (i + 1))[0]);
        }

        var first_day = day - (date % 7 - 1);
        var box_date = 1;

        if (first_day === 0) {
            box_date++;
            first_day++;
        }
        
        for (var col = 0; col < first_day - 1; col++) {
            day_boxes[col].innerHTML = "";
        }
        
        var selected_date = null;
        
        for (var col = first_day - 1; col < 5; col++) {
            day_boxes[col].innerHTML = "0" + box_date;
            
            if (d.getFullYear() === today.getFullYear()
                && d.getMonth() === today.getMonth()
                && today.getDate() === box_date) {
                day_boxes[col].className = "day_box_select";
                selected_date = day_boxes[col];
            }
            
            box_date++;
        }
        
        box_date += 2;

        for (var row = 1; row < 5; row++) {
            for (var col = 1; col < 6; col++) {
                if (box_date > num_days[month]) {
                    day_boxes[row * 5 + (col - 1)].innerHTML = "";
                } else if (box_date < 10) {
                    day_boxes[row * 5 + (col - 1)].innerHTML = "0" + box_date;
                } else {
                    day_boxes[row * 5 + (col - 1)].innerHTML = box_date;
                }
                
                if (d.getFullYear() === today.getFullYear()
                    && d.getMonth() === today.getMonth()
                    && today.getDate() === box_date) {
                    day_boxes[col - 1 + row * 5].className = "day_box_select";
                    selected_date = day_boxes[col - 1 + row * 5];
                }

                box_date++;
            }

            box_date += 2;
        }
        
        function nextMonth() {
            var newD = null;
            
            if (d.getMonth() === 11) {
                newD = new Date(d.getFullYear() + 1, 0, 1);
            } else {
                newD = new Date(d.getFullYear(), d.getMonth() + 1, 1);
            }
            
            if (selected_date) {
                selected_date.className = "day_box";
            }
            
            initializeCalendar(newD);
        }
        
        rightButton.addEventListener("click", nextMonth);
        
        function prevMonth() {
            var newD = null;
            
            if (d.getMonth === 0) {
                newD = new Date(d.getFullYear() - 1, 0, 1);
            } else {
                newD = new Date(d.getFullYear(), d.getMonth() - 1, 1);
            }
            
            if (selected_date) {
                selected_date.className = "day_box";
            }
            
            initializeCalendar(newD);
        }
        
        leftButton.addEventListener("click", prevMonth);
    }
    
    initializeCalendar(d);
    
    function initializeImport() {
        var import_button = $("#import_button")[0];
        
        function importClick() {
            console.log("IMPORT_BUTTON_CLICKED");
        }
        
        $(import_button).click(importClick);
    }
    
    initializeImport();
    
    function initializeUpdate() {
        var update_button = $("#update_button")[0];
        
        function updateClick() {
            console.log("UPDATE BUTTON CLICKED");
        }
        
        $(update_button).click(updateClick);
    }
    
    initializeUpdate();
});