// CAPITALIZES THE FIRST LETTER OF A WORD
function toTitleCase(str) {
    return str.replace (
        /\w\S*/g,
        function(txt) {
            return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
        }
    );
}

// SCHEDULES A REMINDER EMAIL FOR AN APPOINTMENT
function scheduleReminder(address, appointment) {
    var firstName = "";
    
    // LOOKS FOR THE DOT IN THE ADDRESS, AND CREATES THE FIRST NAME SUBSTRING
    for (var i = 0; i < address.length; i++) {
        var character = address[i];
        
        if (character === '.') {
            break;
        } else {
            firstName += character;
        }
    }
    
    firstName = toTitleCase(firstName);
    
    console.log(firstName);
    
    return;
    
    $.ajax({
        type: "POST",
        url: "https://mandrillapp.com/api/1.0/messages/send.json",
        data: {
          'key': "Aez_PE6Q1PErkF32bcxkGA",
          'message': {
            'from_email': 'austin.borger@stonybrook.edu',
            'to': [
                {
                  'email': address,
                  'name': firstName,
                  'type': 'to'
                }
              ],
            'autotext': 'true',
            'subject': 'Reminder',
            'html': 'YOUR EMAIL CONTENT HERE! YOU CAN USE HTML!'
          }
        }
    }).done(function(response) {
         console.log(response); // if you're into that sorta thing
    });
}

$(document).ready(function(){
    scheduleReminder("austin.borger@stonybrook.edu");
});