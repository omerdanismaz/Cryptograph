document.getElementById("authentication-form").addEventListener("submit", function(event)
{
    event.preventDefault();

    const informationMessage = document.getElementById("information-message");
    const firstName = document.getElementById("first-name").value.trim();
    const lastName = document.getElementById("last-name").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const confirmPassword = document.getElementById("confirm-password").value.trim();

    function isAlphaUnicode(string)
    {
        return /^[\p{L}\s]+$/u.test(string);
    }

    function isEmail(string)
    {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(string);
    }

    let doNotSubmit = false;

    if(!firstName || firstName.length > 32)
    {
        doNotSubmit = true;
        informationMessage.textContent = messageOne;
        informationMessage.classList.remove("hidden");
    }
    else if(!isAlphaUnicode(firstName))
    {
        doNotSubmit = true;
        informationMessage.textContent = messageTwo;
        informationMessage.classList.remove("hidden");
    }
    else if(!lastName || lastName.length > 32)
    {
        doNotSubmit = true;
        informationMessage.textContent = messageThree;
        informationMessage.classList.remove("hidden");
    }
    else if(!isAlphaUnicode(lastName))
    {
        doNotSubmit = true;
        informationMessage.textContent = messageFour;
        informationMessage.classList.remove("hidden");
    }
    else if(!email || email.length > 64)
    {
        doNotSubmit = true;
        informationMessage.textContent = messageFive;
        informationMessage.classList.remove("hidden");
    }
    else if(!isEmail(email))
    {
        doNotSubmit = true;
        informationMessage.textContent = messageSix;
        informationMessage.classList.remove("hidden");
    }
    else if(password.length < 8 || password.length > 32)
    {
        doNotSubmit = true;
        informationMessage.textContent = messageEight;
        informationMessage.classList.remove("hidden");
    }
    else if(password != confirmPassword)
    {
        doNotSubmit = true;
        informationMessage.textContent = messageNine;
        informationMessage.classList.remove("hidden");
    }
    else
    {
        informationMessage.classList.add("hidden");
    }

    $.ajax({
        url: "/REST/checkUserExistence",
        type: "POST",
        contentType: "application/x-www-form-urlencoded",
        data: $.param({requestData: email}),
        success: function(response)
        {
            if(response.responseData)
            {
                if(!doNotSubmit)
                {
                    doNotSubmit = true;
                    informationMessage.textContent = messageSeven;
                    informationMessage.classList.remove("hidden");
                }
            }
            else
            {
                if(!doNotSubmit)
                {
                    informationMessage.classList.add("hidden");
                    document.getElementById("authentication-form").submit();
                }
            }
        },
        error: function(error)
        {
            console.error("AJAX Error:", error);
        }
    });
});
