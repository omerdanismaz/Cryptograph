document.getElementById("authentication-form").addEventListener("submit", function(event)
{
    event.preventDefault();

    const informationMessage = document.getElementById("information-message");
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    function isEmail(string)
    {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(string);
    }

    let doNotSubmit = false;

    if(!email || email.length > 64)
    {
        doNotSubmit = true;
        informationMessage.textContent = messageOne;
        informationMessage.classList.remove("hidden");
    }
    else if(!isEmail(email))
    {
        doNotSubmit = true;
        informationMessage.textContent = messageTwo;
        informationMessage.classList.remove("hidden");
    }
    else if(password.length < 8 || password.length > 32)
    {
        doNotSubmit = true;
        informationMessage.textContent = messageThree;
        informationMessage.classList.remove("hidden");
    }
    else
    {
        informationMessage.classList.add("hidden");
    }

    $.ajax({
        url: "/REST/checkUserPassword",
        type: "POST",
        contentType: "application/x-www-form-urlencoded",
        data: $.param({requestDataOne: email, requestDataTwo: password}),
        success: function(response)
        {
            if(response.responseData)
            {
                if(!doNotSubmit)
                {
                    document.getElementById("authentication-form").submit();
                }
            }
            else
            {
                if(!doNotSubmit)
                {
                    doNotSubmit = true;
                    informationMessage.textContent = messageFour;
                    informationMessage.classList.remove("hidden");
                }
            }
        },
        error: function(error)
        {
            console.error("AJAX Error:", error);
        }
    });
});
