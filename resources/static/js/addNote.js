document.getElementById("note-form").addEventListener("submit", function(event)
{
    event.preventDefault();

    const informationMessage = document.getElementById("information-message");
    const noteName = document.getElementById("note-name").value.trim();
    const noteContent = document.getElementById("note-content").value.trim();

    if(!noteName || noteName.length > 64)
    {
        informationMessage.textContent = messageOne;
        informationMessage.classList.remove("hidden");
    }
    else if(!noteContent || noteContent.length > 1024)
    {
        informationMessage.textContent = messageTwo;
        informationMessage.classList.remove("hidden");
    }
    else
    {
        informationMessage.classList.add("hidden");
        document.getElementById("note-form").submit();
    }
});
