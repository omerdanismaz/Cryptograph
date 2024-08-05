document.getElementById("account-form").addEventListener("submit", function(event)
{
    event.preventDefault();

    const informationMessage = document.getElementById("information-message");
    const accountName = document.getElementById("account-name").value.trim();
    const accountUsername = document.getElementById("account-username").value.trim();
    const accountPassword = document.getElementById("account-password").value.trim();

    if(!accountName || accountName.length > 64)
    {
        informationMessage.textContent = messageOne;
        informationMessage.classList.remove("hidden");
    }
    else if(!accountUsername || accountUsername.length > 64)
    {
        informationMessage.textContent = messageTwo;
        informationMessage.classList.remove("hidden");
    }
    else if(!accountPassword || accountPassword.length > 64)
    {
        informationMessage.textContent = messageThree;
        informationMessage.classList.remove("hidden");
    }
    else
    {
        informationMessage.classList.add("hidden");
        document.getElementById("account-form").submit();
    }
});
