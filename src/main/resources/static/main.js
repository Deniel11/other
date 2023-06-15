'use strict';
document.addEventListener('DOMContentLoaded', async () => {
    let userData = await createGetRequest("/actual-user");

    if(userData.error || userData.message) {
        window.location.href = "/register";
    }
    let nameInput = document.getElementById("name-input");
    nameInput.value = userData.name;

    const updateButton = document.getElementById("update-button");
    let outputMessage = document.getElementById("output-message");
    updateButton.addEventListener("click", async (event) => {
        event.preventDefault();
        let sendData = {
            id: userData.id,
            name: nameInput.value
        };
        let updateMessage = await createRequest('PUT', "/update/" + userData.name, sendData);
        if(updateMessage.error){
            outputMessage.innerText = updateMessage.error;
            outputMessage.style.visibility = "visible";
        } else {
            outputMessage.style.visibility = "hidden";
            userData.name = updateMessage.name;
        }
    });

    const sendButton = document.getElementById("send-button");
    let textArea = document.getElementById("text-area");
    let chatSpace = document.getElementById("chat-space");
    sendButton.addEventListener("click", async (event) => {
        event.preventDefault();
        if (textArea.value != "") {
            let sendData = {
                text: textArea.value,
                username: userData.name
            };
            let messageObject = await createRequest('POST', "/save-message", sendData);
            await createRequest("POST","/send-message");
            if(messageObject.error){
                outputMessage.innerText = messageObject.error;
                outputMessage.style.visibility = "visible";
            } else {
                outputMessage.style.visibility = "hidden";
                addMessage(messageObject);
                textArea.value = "";
            }
        }
    });

    let messages = await createGetRequest("/messages");
    if(messages.error){
        outputMessage.innerText = messages.error;
        outputMessage.style.visibility = "visible";
    } else {
        outputMessage.style.visibility = "hidden";
        for (const message of messages) {
            addMessage(message);
        }
    }

    function addMessage(message){
        chatSpace.innerHTML +=  "<li>" +
            message.username +
            "<p>" + message.text + "</p>" +
            "</li>";
    }

    setInterval(async () => {
        const needChange = await createGetRequest("/check-change");
        if (needChange) {
            const lastMessage = await createGetRequest("/get-last-message");
            addMessage(lastMessage);
        }
    }, 1000);
});