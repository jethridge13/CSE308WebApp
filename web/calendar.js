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
        
        function initBox(i) {
            var element = $("#day_box" + (i + 1));
            
            element[0].hasEvent = true;
            
            element.dblclick(function(){
                var event = new Event("day_click");
                event.element = element[0];
                $("#calendar")[0].dispatchEvent(event);
            });
            
            day_boxes.push(element[0]);
        }

        // INITIALIZE DAY BOX REFERENCES
        for (var i = 0; i < 25; i++) {
            initBox(i);
        }

        var first_day = day - (date % 7 - 1);
        var box_date = 1;

        if (first_day === 0) { // SUNDAY = 01
            box_date++;
            first_day++;
        } else if (first_day === 6) { // SATURDAY = 01
            box_date += 2;
            first_day = 1;
        } else {
            for (var col = 0; col < first_day - 1; col++) {
                day_boxes[col].innerHTML = "";
                day_boxes[col].hasEvent = false;
            }
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
                    day_boxes[row * 5 + (col - 1)].hasEvent = false;
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
        
        for (var i = 0; i < 25; i++) {
            if (day_boxes[i].hasEvent) {
                day_boxes[i].innerHTML += "<table class='event table'><tr><th>Exams Scheduled...</th></tr></table>";
                $(day_boxes[i]).css("cursor", "pointer");
            }
        }
        
        var event = new Event("initialized");
        
        $("#calendar")[0].dispatchEvent(event);
    }
    
    initializeCalendar(d);
});